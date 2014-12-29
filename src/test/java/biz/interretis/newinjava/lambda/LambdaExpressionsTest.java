package biz.interretis.newinjava.lambda;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.synchronizedList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import biz.interretis.newinjava.Person;
import biz.interretis.newinjava.Person.Sex;
import biz.interretis.newinjava.PersonCompariser;
import biz.interretis.newinjava.PersonGenerator;

public class LambdaExpressionsTest {

    private PersonGenerator generator;

    @Before
    public void setup() {
        generator = new PersonGenerator();
    }

    private static <T> void processPersons
            (
                    final Collection<Person> people,
                    final Predicate<Person> predicate,
                    final Function<Person, T> mapper,
                    final Consumer<T> block
            )
    {

        for (final Person person : people) {
            if (predicate.test(person)) {
                final T data = mapper.apply(person);
                block.accept(data);
            }
        }
    }

    @Test
    public void generic_processing_function() {

        // given
        final Collection<Person> people = generator.randomList(100);
        final Collection<String> women = synchronizedList(newArrayList());

        // when
        processPersons(
                people,
                person -> person.getGender() == Sex.FEMALE,
                person -> person.getEmailAddress(),
                email -> women.add(email)
        //
        );

        // then
        assertThat(women, hasSize(47));
    }

    @Test
    public void generic_processing_stream() {

        // given
        final Collection<Person> people = generator.randomList(100);
        final Collection<String> women = synchronizedList(newArrayList());

        // when
        people.parallelStream(
                ).filter(
                        person -> person.getGender() == Sex.FEMALE
                ).map(
                        person -> person.getEmailAddress()
                ).forEach(
                        email -> women.add(email)
                );

        // then
        assertThat(women, hasSize(47));
    }

    @Test
    public void generic_processing_declared() {

        // given
        final Collection<Person> people = generator.randomList(100);
        final Collection<String> women = synchronizedList(newArrayList());

        // when
        final Predicate<? super Person> womenOnly =
                person -> person.getGender() == Sex.FEMALE;

        final Function<? super Person, ? extends String> personsEmail =
                Person::getEmailAddress;

        final Consumer<? super String> addToCollection =
                (email) -> women.add(email);

        people.parallelStream().filter(womenOnly).map(personsEmail).forEach(addToCollection);

        // then
        assertThat(women, hasSize(47));
    }

    @Test
    public void comparing_with_static_method_reference() {

        // given
        final List<Person> people = generator.randomList(100);

        // when
        Collections.sort(people, Person::compareByAge);

        // then
        final Person first = people.get(0);
        final Person second = people.get(1);

        assertThat(first.getBirthday(), is(lessThan(second.getBirthday())));
    }

    @Test
    public void comparing_with_instance_method_reference_of_particular_object() {

        // given
        final List<Person> people = generator.randomList(100);
        final PersonCompariser compariser = new PersonCompariser();

        // when
        Collections.sort(people, compariser::compareByName);

        // then
        final Person second = people.get(1);
        final Person third = people.get(2);

        assertThat(second.getName(), is(lessThan(third.getName())));
    }

    @Test
    public void comparing_with_instance_method_reference_of_particular_type() {

        // given
        final List<Person> people = generator.randomList(100);
        final List<String> emails = people
                .stream()
                .map(Person::getEmailAddress)
                .sorted()
                .collect(Collectors.toList());

        // when
        Collections.sort(emails, String::compareToIgnoreCase);

        // then
        final String second = emails.get(1);
        final String third = emails.get(2);

        assertThat(second, is(lessThan(third)));
    }

    public static <ELEMENT, SOURCE extends Collection<ELEMENT>, TARGET extends Collection<ELEMENT>>
            TARGET transferElements
            (
                    final SOURCE sourceCollection,
                    final Supplier<TARGET> collectionFactory
            )
    {
        final TARGET targetCollection = collectionFactory.get();
        for (final ELEMENT element : sourceCollection) {
            targetCollection.add(element);
        }
        return targetCollection;
    }

    @Test
    public void using_supplier() {

        // given
        final List<Person> people = generator.randomList(100);

        // when
        final Set<Person> unique = transferElements(people,
                () -> {
                    return new HashSet<>();
                });

        // then
        assertThat(unique, hasSize(100));
    }

    @Test
    public void using_constructor_reference_for_supplier() {

        // given
        final List<Person> people = generator.randomList(100);

        // when
        final Set<Person> unique = transferElements(people, HashSet<Person>::new);

        // then
        assertThat(unique, hasSize(100));
    }

    @Test
    public void grouping_by_gender() {

        // given
        final List<Person> people = generator.randomList(100);

        // when
        final Map<Sex, List<Person>> peopleByGender = people.stream()
                .collect(
                        Collectors.groupingBy(Person::getGender)
                );

        // then
        assertThat(peopleByGender.get(Sex.FEMALE), hasSize(47));
        assertThat(peopleByGender.get(Sex.MALE), hasSize(53));
    }

    @Test
    public void grouping_by_two_criteria() {

        // given
        final List<Person> people = generator.randomList(100);

        // when
        final Map<Sex, Map<String, List<Person>>> peopleBySexAndName = people.stream()
                .collect(
                        Collectors.groupingBy(
                                Person::getGender,
                                Collectors.groupingBy(Person::getName)
                                )
                );

        // then
        final List<Person> kentons = peopleBySexAndName.get(Sex.MALE).get("Kenton");
        assertThat("There are four Kentons population", kentons, hasSize(4));
    }
}

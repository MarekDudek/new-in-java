package biz.interretis.newinjava.lambda;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.synchronizedList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import biz.interretis.newinjava.Person;
import biz.interretis.newinjava.Person.Sex;
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
        final Collection<Person> people = generator.randomCollection(100);
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
        final Collection<Person> people = generator.randomCollection(100);
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
}

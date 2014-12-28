package biz.interretis.newinjava.lambda;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
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

    private static void processPersons
            (
                    final Collection<Person> people,
                    final Predicate<Person> predicate,
                    final Consumer<Person> block
            )
    {

        for (final Person person : people) {
            if (predicate.test(person)) {
                block.accept(person);
            }
        }
    }

    @Test
    public void printing() {

        // given
        final Collection<Person> people = generator.randomCollection(100);
        final List<Person> women = newArrayList();

        // when
        processPersons(
                people,
                person -> person.getGender() == Sex.FEMALE,
                person -> women.add(person));

        // then
        assertThat(women, hasSize(47));
    }
}

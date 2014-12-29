package biz.interretis.newinjava.lambda;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import biz.interretis.newinjava.Person;
import biz.interretis.newinjava.PersonGenerator;

public class PersonGeneratorTest {

    @Test
    public void person_is_generated_properly() {

        // given
        final PersonGenerator generator = new PersonGenerator();

        // when
        final Person person = generator.randomPerson();

        // then
        assertThat(person, notNullValue());
        assertThat(person.getName(), notNullValue());
        assertThat(person.getBirthday(), notNullValue());
        assertThat(person.getGender(), notNullValue());
        assertThat(person.getEmailAddress(), notNullValue());
    }

    @Test
    public void long_number_of_persons_is_generated_properly() {

        // given
        final PersonGenerator generator = new PersonGenerator();
        final int size = 10_000;

        // when
        final Collection<Person> people = generator.randomList(size);

        // then
        assertThat(people, hasSize(size));
    }
}

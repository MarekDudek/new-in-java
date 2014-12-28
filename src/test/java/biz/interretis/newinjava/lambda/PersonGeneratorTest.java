package biz.interretis.newinjava.lambda;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

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

        // when
        for (int i = 0; i < 10_000; i++) {
            generator.randomPerson();
        }

        // then it doesn't break
    }
}

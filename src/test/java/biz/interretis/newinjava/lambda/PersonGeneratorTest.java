package biz.interretis.newinjava.lambda;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import biz.interretis.newinjava.Person;
import biz.interretis.newinjava.PersonGenerator;

public class PersonGeneratorTest {

    @Test
    public void test() {

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
}

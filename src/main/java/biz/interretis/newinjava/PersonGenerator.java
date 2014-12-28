package biz.interretis.newinjava;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import biz.interretis.newinjava.Person.Sex;

import com.google.common.collect.Sets;

public class PersonGenerator {

    private static final String[] MALE_NAMES = {
            "Jorge", "Ellsworth", "Maximo", "Richard", "Paul", "Edgardo", "Grant", "Ernie", "Dominic", "Abe",
            "Jonas", "Rodrick", "Erin", "Antonio", "Josh", "Lucius", "Damien", "Kenton", "Milford", "Jackie",
    };

    static {
        assert MALE_NAMES.length == Sets.newHashSet(MALE_NAMES).size();
    }

    private static final int MAX_YEAR = 2014;
    private static final int MIN_YEAR = 1910;

    private final Random generator = new Random();

    public Person randomPerson() {

        final Person person = new Person();

        person.setGender(randomGender());
        person.setName(randomName());
        person.setBirthday(randomDate());
        person.setEmailAddress(randomEmailAddress(person));

        return person;
    }

    private String randomName() {
        return MALE_NAMES[generator.nextInt(MALE_NAMES.length)];
    }

    private LocalDate randomDate() {

        final int year = randomIntBetweenInclusive(MIN_YEAR, MAX_YEAR);
        final int month = randomIntBetweenInclusive(Month.JANUARY.getValue(), Month.DECEMBER.getValue());

        final int lastDay = new GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        final int day = randomIntBetweenInclusive(1, lastDay);

        return LocalDate.of(year, month, day);
    }

    private int randomIntBetweenInclusive(final int lowerBound, final int upperBound) {
        final int span = upperBound - lowerBound + 1;
        final int positiveBelowSpan = generator.nextInt(span);
        return positiveBelowSpan + lowerBound;
    }

    private Sex randomGender() {
        final int zeroOrOne = generator.nextInt(2);
        return Sex.values()[zeroOrOne];
    }

    private String randomEmailAddress(final Person person) {
        return String.format("%s@gmail.com", person.getName());
    }
}

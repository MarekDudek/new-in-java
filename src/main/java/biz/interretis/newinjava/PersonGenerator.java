package biz.interretis.newinjava;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import biz.interretis.newinjava.Person.Sex;

import com.google.common.collect.Sets;

public class PersonGenerator {

    private static final String[] FEMALE_NAMES = {
            "Virginia", "Yvette", "Sally", "Tara", "Vickie", "Judy", "Eleanor", "Julia", "Jaime", "Alicia",
            "Marianna", "Christie", "Melissa", "Paulette", "Annette", "Lauren", "Erika", "Beatrice", "Hannah", "Eilene",
    };

    static {
        assert FEMALE_NAMES.length == Sets.newHashSet(FEMALE_NAMES).size();
    }

    private static final String[] MALE_NAMES = {
            "Jorge", "Ellsworth", "Maximo", "Richard", "Paul", "Edgardo", "Grant", "Ernie", "Dominic", "Abe",
            "Jonas", "Rodrick", "Chris", "Antonio", "Josh", "Lucius", "Damien", "Kenton", "Milford", "Wendell",
    };

    static {
        assert MALE_NAMES.length == Sets.newHashSet(MALE_NAMES).size();
    }

    private static final Calendar CALENDAR = new GregorianCalendar();
    private static final int MINIMUM_DAY = CALENDAR.getMinimum(Calendar.DAY_OF_MONTH);
    private static final int MAXIMUM_DAY = CALENDAR.getMaximum(Calendar.DAY_OF_MONTH);

    private static final int MAX_YEAR = 2014;
    private static final int MIN_YEAR = 1910;

    private final Random generator = new Random(0);

    public Person randomPerson() {

        final Person person = new Person();

        person.setGender(randomGender());
        person.setName(randomName(person));
        person.setBirthday(randomDate());
        person.setEmailAddress(randomEmailAddress(person));

        return person;
    }

    private String randomName(final Person person) {

        switch (person.getGender()) {
        case FEMALE:
            return FEMALE_NAMES[generator.nextInt(FEMALE_NAMES.length)];
        case MALE:
            return MALE_NAMES[generator.nextInt(MALE_NAMES.length)];
        default:
            throw new IllegalArgumentException();
        }
    }

    private LocalDate randomDate() {

        final int year = randomIntBetweenInclusive(MIN_YEAR, MAX_YEAR);
        assert MIN_YEAR <= year && year <= MAX_YEAR;

        final int month = randomIntBetweenInclusive(Month.JANUARY.getValue(), Month.DECEMBER.getValue());
        assert Month.JANUARY.getValue() <= month && month <= Month.DECEMBER.getValue();

        final GregorianCalendar calendar = new GregorianCalendar(year, month - 1, 1);
        final int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        final int day = randomIntBetweenInclusive(1, actualMaximum);
        assert MINIMUM_DAY <= day && day <= MAXIMUM_DAY;

        return LocalDate.of(year, month, day);
    }

    private int randomIntBetweenInclusive(final int lowerBound, final int upperBound) {

        final int span = upperBound - lowerBound + 1;
        final int positiveBelowSpan = generator.nextInt(span);

        final int numberBetweenBounds = positiveBelowSpan + lowerBound;
        assert lowerBound <= numberBetweenBounds && numberBetweenBounds <= upperBound;

        return numberBetweenBounds;
    }

    private Sex randomGender() {
        final int zeroOrOne = generator.nextInt(2);
        return Sex.values()[zeroOrOne];
    }

    private String randomEmailAddress(final Person person) {
        return String.format("%s@gmail.com", person.getName().toLowerCase());
    }
}

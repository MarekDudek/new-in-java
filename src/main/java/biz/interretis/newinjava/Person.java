package biz.interretis.newinjava;

import java.time.LocalDate;

/**
 * Person.
 * 
 * @author marek
 */
public final class Person {

    /**
     * Sex.
     * 
     * @author marek
     */
    public enum Sex {
        MALE, FEMALE
    }

    /** Name. */
    private String name;
    /** Birthday. */
    private LocalDate birthday;
    /** Gender. */
    private Sex gender;
    /** E-mail address. */
    private String emailAddress;

    /** Name getter. */
    public String getName() {
        return name;
    }

    /** Name setter. */
    public void setName(final String aName) {
        this.name = aName;
    }

    /** Birthday getter. */
    public LocalDate getBirthday() {
        return birthday;
    }

    /** Birthday setter. */
    public void setBirthday(final LocalDate aBirthday) {
        this.birthday = aBirthday;
    }

    /** Gender getter. */
    public Sex getGender() {
        return gender;
    }

    /** Gender setter. */
    public void setGender(final Sex aGender) {
        this.gender = aGender;
    }

    /** E-mail getter. */
    public String getEmailAddress() {
        return emailAddress;
    }

    /** E-mail setter. */
    public void setEmailAddress(final String aEmailAddress) {
        this.emailAddress = aEmailAddress;
    }
}

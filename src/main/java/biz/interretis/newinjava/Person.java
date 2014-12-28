package biz.interretis.newinjava;

import java.time.LocalDate;

import com.google.common.base.MoreObjects;

public final class Person {

    public enum Sex {
        MALE, FEMALE
    }

    private String name;
    private LocalDate birthday;
    private Sex gender;
    private String emailAddress;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("birthday", birthday)
                .add("gender", gender)
                .add("e-mail", emailAddress)
                .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(final String aName) {
        this.name = aName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(final LocalDate aBirthday) {
        this.birthday = aBirthday;
    }

    public Sex getGender() {
        return gender;
    }

    public void setGender(final Sex aGender) {
        this.gender = aGender;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String aEmailAddress) {
        this.emailAddress = aEmailAddress;
    }
}

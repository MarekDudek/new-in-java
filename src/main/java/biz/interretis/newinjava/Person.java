package biz.interretis.newinjava;

import java.time.LocalDate;

public final class Person {

    public enum Sex {
	MALE, FEMALE
    }

    private String name;
    private LocalDate birthday;
    private Sex gender;
    private String emailAddress;

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

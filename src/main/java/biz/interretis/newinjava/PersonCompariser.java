package biz.interretis.newinjava;

public class PersonCompariser {

    public int compareByName(final Person first, final Person second) {
        return first.getName().compareTo(second.getName());
    }
}

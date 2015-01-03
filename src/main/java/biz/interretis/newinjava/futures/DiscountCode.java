package biz.interretis.newinjava.futures;

public enum DiscountCode {

    NONE(0),
    SILVER(5),
    GOLD(10),
    PLATINIUM(15),
    DIAMOND(20);

    public final int percentage;

    private DiscountCode(final int percentage) {
        this.percentage = percentage;
    }
}

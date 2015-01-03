package biz.interretis.newinjava.futures;

public class Quote {

    private final String shopName;
    private final double price;
    private final DiscountCode code;

    public Quote(final String shopName, final double price, final DiscountCode code) {
        this.shopName = shopName;
        this.price = price;
        this.code = code;
    }

    public static Quote parse(final String quoteString) {

        final String[] tokens = quoteString.split(":");

        final String shopName = tokens[0];
        final double price = Double.parseDouble(tokens[1]);
        final DiscountCode code = DiscountCode.valueOf(tokens[2]);

        return new Quote(shopName, price, code);
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public DiscountCode getCode() {
        return code;
    }
}

package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.delay;
import static java.lang.String.format;

import java.time.Duration;

public class DiscountService {

    public String applyDiscount(final Quote quote, final Duration duration) {

        return format(
                "%s price is %.2f",
                quote.getShopName(),
                applyDiscount(quote.getPrice(), quote.getCode(), duration));
    }

    private double applyDiscount(final double price, final DiscountCode code, final Duration duration) {

        delay(duration);

        return price * (100 - code.percentage) / 100;
    }
}

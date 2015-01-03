package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.delay;
import static biz.interretis.newinjava.futures.DiscountCode.values;
import static java.lang.String.format;

import java.time.Duration;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {

    private static final Random GENERATOR = new Random(0);

    private final String name;

    public Shop(final String name) {
        this.name = name;
    }

    public double getPrice(final String product, final Duration duration) {

        return calculatePrice(product, duration);
    }

    public String getPriceAndDiscountCode(final String product, final Duration duration) {

        final double price = calculatePrice(product, duration);
        final DiscountCode code = values()[GENERATOR.nextInt(values().length)];

        return format(Locale.US, "%s:%.2f:%s", name, price, code);
    }

    public Future<Double> getPriceAsynch(final String product, final Duration duration) {

        final CompletableFuture<Double> future = new CompletableFuture<>();

        new Thread(() -> {

            final double price = calculatePrice(product, duration);
            future.complete(price);

        }, name).start();

        return future;
    }

    public Future<Double> getPriceAsynchErrorPropagated(final String product, final Duration duration) {

        return CompletableFuture.supplyAsync(() -> calculatePrice(product, duration));
    }

    private double calculatePrice(final String product, final Duration duration) {

        delay(duration);

        return GENERATOR.nextDouble() * product.charAt(0) * product.charAt(1);
    }

    public String getName() {
        return name;
    }
}

package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.delay;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {

    private static final Random GENERATOR = new Random(0);

    private final String name;

    public Shop(final String name) {
        this.name = name;
    }

    public double getPrice(final String product) {
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsynch(final String product) {

        final CompletableFuture<Double> futurePrice = new CompletableFuture<Double>();

        new Thread(() -> {
            final double price = calculatePrice(product);
            futurePrice.complete(price);
        }, name).start();

        return futurePrice;
    }

    private double calculatePrice(final String product) {
        delay();
        return GENERATOR.nextDouble() * product.charAt(0) * product.charAt(1);
    }
}

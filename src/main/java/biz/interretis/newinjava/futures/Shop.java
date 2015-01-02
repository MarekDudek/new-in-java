package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.delay;

import java.time.Duration;
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

    public Future<Double> getPriceAsynch(final String product, final Duration duration) {

        final CompletableFuture<Double> future = new CompletableFuture<>();

        new Thread(() -> {

            final double price = calculatePrice(product, duration);
            future.complete(price);

        }, name).start();

        return future;
    }

    public Future<Double> getPriceAsynchErrorPropagated(final String product, final Duration duration) {

        final CompletableFuture<Double> future = new CompletableFuture<>();

        new Thread(() -> {
            try {
                final double price = calculatePrice(product, duration);
                future.complete(price);
            } catch (final Exception ex) {
                future.completeExceptionally(ex);
            }

        }, name).start();

        return future;
    }

    private double calculatePrice(final String product, final Duration duration) {

        delay(duration);

        return GENERATOR.nextDouble() * product.charAt(0) * product.charAt(1);
    }
}

package biz.interretis.newinjava.futures;

import static java.lang.String.format;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShopService {

    private static final String PRICE_FORMAT = "%s price is %.2f";

    public List<String> findPricesSerial(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .stream()
                .map(shopToPrice(product, duration))
                .collect(Collectors.toList());
    }

    public List<String> findPricesParallel(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .parallelStream()
                .map(shopToPrice(product, duration))
                .collect(Collectors.toList());
    }

    public List<CompletableFuture<String>> findPriceFutures(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .stream()
                .map(shopToFuturePrice(product, duration))
                .collect(Collectors.toList());
    }

    public List<CompletableFuture<String>> findPriceFutures(final List<Shop> shops, final String product, final Duration duration, final Executor executor) {

        return shops
                .stream()
                .map(shopToFuturePriceExecutor(product, duration, executor))
                .collect(Collectors.toList());
    }

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration duration) {

        final List<CompletableFuture<String>> priceFutures = shops
                .stream()
                .map(shopToFuturePrice(product, duration))
                .collect(Collectors.toList());

        return priceFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration duration, final Executor executor) {

        final List<CompletableFuture<String>> priceFutures = shops
                .stream()
                .map(shopToFuturePriceExecutor(product, duration, executor))
                .collect(Collectors.toList());

        return priceFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesBroken(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .stream()
                .map(shopToFuturePrice(product, duration))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesFixed(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .parallelStream()
                .map(shopToFuturePrice(product, duration))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesFixed(final List<Shop> shops, final String product, final Duration duration, final Executor executor) {

        return shops
                .parallelStream()
                .map(shopToFuturePriceExecutor(product, duration, executor))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private static Function<Shop, String> shopToPrice(final String product, final Duration duration) {

        return shop -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration));
    }

    private static Function<Shop, CompletableFuture<String>> shopToFuturePrice(final String product, final Duration duration) {

        return shop -> CompletableFuture.supplyAsync(
                () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration))
                );
    }

    private static Function<Shop, CompletableFuture<String>> shopToFuturePriceExecutor(final String product, final Duration duration, final Executor executor) {

        return shop -> CompletableFuture.supplyAsync
                (
                        () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration)),
                        executor
                );
    }
}

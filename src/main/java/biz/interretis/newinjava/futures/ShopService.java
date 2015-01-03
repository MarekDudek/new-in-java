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

    private final DiscountService discountService = new DiscountService();

    public List<String> findPricesSerial(final List<Shop> shops, final String product, final Duration shopDelay) {

        return shops
                .stream()
                .map(shopToPrice(product, shopDelay))
                .collect(Collectors.toList());
    }

    public List<String> findPricesParallel(final List<Shop> shops, final String product, final Duration shopDelay) {

        return shops
                .parallelStream()
                .map(shopToPrice(product, shopDelay))
                .collect(Collectors.toList());
    }

    public List<CompletableFuture<String>> findPriceFutures(final List<Shop> shops, final String product, final Duration shopDelay) {

        return shops
                .stream()
                .map(shopToFuturePrice(product, shopDelay))
                .collect(Collectors.toList());
    }

    public List<CompletableFuture<String>> findPriceFutures(final List<Shop> shops, final String product, final Duration shopDelay, final Executor executor) {

        return shops
                .stream()
                .map(shopToFuturePriceExecutor(product, shopDelay, executor))
                .collect(Collectors.toList());
    }

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration shopDelay) {

        final List<CompletableFuture<String>> priceFutures = shops
                .stream()
                .map(shopToFuturePrice(product, shopDelay))
                .collect(Collectors.toList());

        return priceFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration shopDelay, final Executor executor) {

        final List<CompletableFuture<String>> priceFutures = shops
                .stream()
                .map(shopToFuturePriceExecutor(product, shopDelay, executor))
                .collect(Collectors.toList());

        return priceFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesBroken(final List<Shop> shops, final String product, final Duration shopDelay) {

        return shops
                .stream()
                .map(shopToFuturePrice(product, shopDelay))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesFixed(final List<Shop> shops, final String product, final Duration shopDelay) {

        return shops
                .parallelStream()
                .map(shopToFuturePrice(product, shopDelay))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesFixed(final List<Shop> shops, final String product, final Duration shopDelay, final Executor executor) {

        return shops
                .parallelStream()
                .map(shopToFuturePriceExecutor(product, shopDelay, executor))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findDiscountedPricesSerial(final List<Shop> shops, final String product, final Duration shopDelay, final Duration discountDelay) {

        return shops.stream()
                .map(shop -> shop.getPriceAndDiscountCode(product, shopDelay))
                .map(Quote::parse)
                .map(quote -> discountService.applyDiscount(quote, discountDelay))
                .collect(Collectors.toList());
    }

    public List<String> findDiscountedPricesParallel(final List<Shop> shops, final String product, final Duration shopDelay, final Duration discountDelay) {

        return shops.parallelStream()
                .map(shop -> shop.getPriceAndDiscountCode(product, shopDelay))
                .map(Quote::parse)
                .map(quote -> discountService.applyDiscount(quote, discountDelay))
                .collect(Collectors.toList());
    }

    public List<String> findDiscountedPricesFutures
            (
                    final List<Shop> shops,
                    final String product,
                    final Duration shopDelay,
                    final Duration discountDelay,
                    final Executor executor) {

        final List<CompletableFuture<String>> futureDiscountedPrices = shops
                .stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPriceAndDiscountCode(product, shopDelay),
                        executor
                        )
                )
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> (CompletableFuture<String>) future.thenCompose(
                        quote -> CompletableFuture.supplyAsync(
                                () -> discountService.applyDiscount(quote, discountDelay),
                                executor
                                )
                        )
                )
                .collect(Collectors.toList());

        return futureDiscountedPrices
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private static Function<Shop, String> shopToPrice(final String product, final Duration shopDelay) {

        return shop -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, shopDelay));
    }

    private static Function<Shop, CompletableFuture<String>> shopToFuturePrice(final String product, final Duration shopDelay) {

        return shop -> CompletableFuture.supplyAsync(
                () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, shopDelay))
                );
    }

    private static Function<Shop, CompletableFuture<String>> shopToFuturePriceExecutor(final String product, final Duration shopDelay, final Executor executor) {

        return shop -> CompletableFuture.supplyAsync
                (
                        () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, shopDelay)),
                        executor
                );
    }
}

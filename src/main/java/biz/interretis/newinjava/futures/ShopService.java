package biz.interretis.newinjava.futures;

import static java.lang.String.format;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ShopService {

    private static final String PRICE_FORMAT = "%s price is %.2f";

    public List<String> findPricesSerial(final List<Shop> shops, final String product, final Duration duration) {

        return shops.stream()
                .map(shop -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration)))
                .collect(Collectors.toList());
    }

    public List<String> findPricesParallel(final List<Shop> shops, final String product, final Duration duration) {

        return shops.parallelStream()
                .map(shop -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration)))
                .collect(Collectors.toList());
    }

    public List<CompletableFuture<String>> findPriceFutures(final List<Shop> shops, final String product, final Duration duration) {

        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration))
                        )
                ).collect(Collectors.toList());
    }

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration duration) {

        final List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration))
                        )
                ).collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}

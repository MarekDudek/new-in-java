package biz.interretis.newinjava.futures;

import static java.lang.String.format;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShopService {

    private static final String PRICE_FORMAT = "%s price is %.2f";

    public List<String> findPricesSerial(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .stream()
                .map(ShopToPriceMapper.shopToPrice(product, duration))
                .collect(Collectors.toList());
    }

    public List<String> findPricesParallel(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .parallelStream()
                .map(ShopToPriceMapper.shopToPrice(product, duration))
                .collect(Collectors.toList());
    }

    public List<CompletableFuture<String>> findPriceFutures(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .stream()
                .map(ShopToFuturePriceMapper.shopToFuturePrice(product, duration))
                .collect(Collectors.toList());
    }

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration duration) {

        final List<CompletableFuture<String>> priceFutures = shops
                .stream()
                .map(ShopToFuturePriceMapper.shopToFuturePrice(product, duration))
                .collect(Collectors.toList());

        return priceFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesBroken(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .stream()
                .map(ShopToFuturePriceMapper.shopToFuturePrice(product, duration))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesFixed(final List<Shop> shops, final String product, final Duration duration) {

        return shops
                .parallelStream()
                .map(ShopToFuturePriceMapper.shopToFuturePrice(product, duration))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private static class ShopToPriceMapper implements Function<Shop, String> {

        public static final ShopToPriceMapper shopToPrice(final String product, final Duration duration) {
            return new ShopToPriceMapper(product, duration);
        }

        private final Duration duration;
        private final String product;

        private ShopToPriceMapper(final String product, final Duration duration) {
            this.product = product;
            this.duration = duration;
        }

        @Override
        public String apply(final Shop shop) {
            return format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration));
        }
    }

    private static class ShopToFuturePriceMapper implements Function<Shop, CompletableFuture<String>> {

        public static final ShopToFuturePriceMapper shopToFuturePrice(final String product, final Duration duration) {
            return new ShopToFuturePriceMapper(product, duration);
        }

        private final Duration duration;
        private final String product;

        private ShopToFuturePriceMapper(final String product, final Duration duration) {
            this.product = product;
            this.duration = duration;
        }

        @Override
        public CompletableFuture<String> apply(final Shop shop) {
            return CompletableFuture.supplyAsync(
                    () -> format(PRICE_FORMAT, shop.getName(), shop.getPrice(product, duration))
                    );
        }
    }
}

package biz.interretis.newinjava.futures;

import static java.lang.String.format;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ShopService {

    public List<String> findPrices(final List<Shop> shops, final String product, final Duration duration) {

        return shops.stream()
                .map(shop -> format("%s price is %.2f", shop.getName(), shop.getPrice(product, duration)))
                .collect(Collectors.<String> toList());
    }
}

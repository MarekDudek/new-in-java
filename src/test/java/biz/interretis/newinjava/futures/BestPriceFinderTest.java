package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_AND_A_TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static com.google.common.collect.Lists.newArrayList;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class BestPriceFinderTest {

    private Shop shop;
    private List<Shop> shops;
    private int shopsCount;

    private ShopService shopService;

    private Clock clock;

    @Before
    public void setup() {

        // given

        shop = new Shop("BestShop");
        shops = newArrayList
                (
                        new Shop("BestPrice"),
                        new Shop("LetsSaveBig"),
                        new Shop("MyFavoriteShop"),
                        new Shop("BuyItAll")// ,
                // new Shop("Shop #5"),
                // new Shop("Shop #6"),
                // new Shop("Shop #7"),
                // new Shop("Shop #8")
                );
        shopsCount = shops.size();

        shopService = new ShopService();

        clock = systemUTC();

    }

    @Test
    public void single_server_asych_api() throws Exception {

        // when
        final Instant start = clock.instant();

        final Future<Double> price = shop.getPriceAsynch("my favorite product", ONE_SECOND);

        final Instant invocationReturned = clock.instant();

        // then
        assertThat(between(start, invocationReturned), lessThan(TENTH_OF_SECOND));

        // when
        doSomethingElse(ONE_SECOND);
        price.get();

        final Instant valueRetrieved = clock.instant();

        // then
        assertThat(
                between(start, valueRetrieved),
                both(greaterThan(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test(expected = TimeoutException.class)
    public void single_server_asych_api__unhandled_error() throws Exception {

        // when
        final Future<Double> price = shop.getPriceAsynch("p", TENTH_OF_SECOND);

        price.get(1, TimeUnit.SECONDS);

        // then exception is thrown
    }

    @Test
    public void single_server_asych_api__handled_error() throws Exception {

        // when
        final Future<Double> price = shop.getPriceAsynchErrorPropagated("p", TENTH_OF_SECOND);

        try {
            price.get(1, TimeUnit.SECONDS);
        } catch (final ExecutionException ex) {
            // then
            final Throwable cause = ex.getCause();
            assertThat(cause, Matchers.instanceOf(StringIndexOutOfBoundsException.class));
        }
    }

    @Test
    public void multiple_servers_synch_api__serial_stream() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findPricesSerial(shops, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(shopsCount));
        assertThat(
                between(start, valuesRetrieved),
                both(greaterThan(Duration.of(shopsCount, SECONDS))).and(lessThan(Duration.of(shopsCount + 1, SECONDS))));
    }

    @Test
    public void multiple_servers_synch_api__parallel_stream() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findPricesParallel(shops, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        final int shopsCount = shops.size();
        assertThat(prices, hasSize(shopsCount));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThan(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_asynch_api__futures() {

        // when
        final Instant start = clock.instant();

        final List<CompletableFuture<String>> priceFutures =
                shopService.findPriceFutures(shops, "my favorite product", ONE_SECOND);

        final Instant invocationReturned = clock.instant();

        // then
        assertThat(priceFutures, hasSize(shopsCount));

        assertThat(between(start, invocationReturned), lessThan(TENTH_OF_SECOND));

        // when
        final List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(shopsCount));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThan(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPrices(shops, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(shopsCount));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThan(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures__broken() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPricesBroken(shops, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(shopsCount));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThan(Duration.of(shopsCount, SECONDS))).and(lessThan(Duration.of(shopsCount + 1, SECONDS))));
    }

    @Test
    public void multiple_servers_synch_api__futures__fixed() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPricesFixed(shops, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(shopsCount));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThan(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }
}

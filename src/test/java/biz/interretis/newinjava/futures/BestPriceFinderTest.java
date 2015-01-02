package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_AND_A_TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TWO_AND_A_FIFTN_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TWO_SECONDS;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static com.google.common.collect.Lists.newArrayList;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class BestPriceFinderTest {

    private Shop shop;

    private List<Shop> shopsHalfOfProcessors;
    private int halfOfProcessors;

    private List<Shop> shopsAllProcessors;
    private int allProcessors;
    private Executor executor;

    private ShopService shopService;

    private Clock clock;

    @Before
    public void setup() {

        // given

        shop = new Shop("BestShop");

        final int availableProcessors = Runtime.getRuntime().availableProcessors();

        shopsHalfOfProcessors = newArrayList();
        for (int i = 0; i < availableProcessors / 2; i++) {
            shopsHalfOfProcessors.add(new Shop("Shop #" + i));
        }
        halfOfProcessors = shopsHalfOfProcessors.size();

        shopsAllProcessors = newArrayList();
        for (int i = 0; i < availableProcessors; i++) {
            shopsAllProcessors.add(new Shop("Shop #" + i));
        }

        allProcessors = shopsAllProcessors.size();
        executor = Executors.newFixedThreadPool(allProcessors, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        });

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
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
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
            assertThat(cause, instanceOf(StringIndexOutOfBoundsException.class));
        }
    }

    @Test
    public void multiple_servers_synch_api__serial_stream() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findPricesSerial(shopsHalfOfProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));
        assertThat(
                between(start, valuesRetrieved),
                both(
                        greaterThanOrEqualTo(Duration.of(halfOfProcessors, SECONDS))).and(
                        lessThan(Duration.of(halfOfProcessors, SECONDS)
                                .plus(Duration.of(halfOfProcessors * 100, MILLIS)))));
    }

    @Test
    public void multiple_servers_synch_api__parallel_stream() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findPricesParallel(shopsHalfOfProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        final int shopsCount = shopsHalfOfProcessors.size();
        assertThat(prices, hasSize(shopsCount));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_asynch_api__futures() {

        // when
        final Instant start = clock.instant();

        final List<CompletableFuture<String>> priceFutures =
                shopService.findPriceFutures(shopsHalfOfProcessors, "my favorite product", ONE_SECOND);

        final Instant invocationReturned = clock.instant();

        // then
        assertThat(priceFutures, hasSize(halfOfProcessors));

        assertThat(between(start, invocationReturned), lessThan(TENTH_OF_SECOND));

        // when
        final List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_asynch_api__futures__all_processors() {

        // when
        final Instant start = clock.instant();

        final List<CompletableFuture<String>> priceFutures =
                shopService.findPriceFutures(shopsAllProcessors, "my favorite product", ONE_SECOND);

        final Instant invocationReturned = clock.instant();

        // then
        assertThat(priceFutures, hasSize(allProcessors));

        assertThat(between(start, invocationReturned), lessThan(TENTH_OF_SECOND));

        // when
        final List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(TWO_SECONDS)).and(lessThan(TWO_AND_A_FIFTN_OF_SECOND)));
    }

    @Test
    public void multiple_servers_asynch_api__futures__all_processors__with_executor() {

        // when
        final Instant start = clock.instant();

        final List<CompletableFuture<String>> priceFutures =
                shopService.findPriceFutures(shopsAllProcessors, "my favorite product", ONE_SECOND, executor);

        final Instant invocationReturned = clock.instant();

        // then
        assertThat(priceFutures, hasSize(allProcessors));

        assertThat(between(start, invocationReturned), lessThan(TENTH_OF_SECOND));

        // when
        final List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPrices(shopsHalfOfProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures__all_processors() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPrices(shopsAllProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(TWO_SECONDS)).and(lessThan(TWO_AND_A_FIFTN_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures__all_processors__with_executor() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPrices(shopsAllProcessors, "my favorite product", ONE_SECOND, executor);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures__broken() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPricesBroken(shopsHalfOfProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(
                        greaterThanOrEqualTo(Duration.of(halfOfProcessors, SECONDS))).and(
                        lessThan(Duration.of(halfOfProcessors, SECONDS)
                                .plus(Duration.of(halfOfProcessors * 100, MILLIS)))));
    }

    @Test
    // processors
    public void multiple_servers_synch_api__futures__fixed() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPricesFixed(shopsHalfOfProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }

    @Test
    public void multiple_servers_synch_api__futures__fixed__all_processors() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPricesFixed(shopsAllProcessors, "my favorite product", ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(
                        greaterThanOrEqualTo(Duration.ofSeconds(4))).and(
                        lessThan(Duration.of(allProcessors / 2, SECONDS)
                                .plus(Duration.of(allProcessors / 2 * 100, MILLIS)))));
    }
}

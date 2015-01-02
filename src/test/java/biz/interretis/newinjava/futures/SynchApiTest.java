package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_AND_A_TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TWO_AND_A_FIFTN_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TWO_SECONDS;
import static com.google.common.collect.Lists.newArrayList;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;

public class SynchApiTest {

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
    public void with_serial_stream() {

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
    public void with_parallel_stream() {

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
    public void with_futures() {

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
    public void with_futures__all_processors() {

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
    public void with_futures__all_processors__with_executor() {

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
    public void with_futures__broken() {

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
    public void with_futures__fixed() {

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
    public void with_futures__fixed__all_processors() {

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
                        greaterThanOrEqualTo(Duration.ofSeconds(3))).and(
                        lessThan(Duration.of(allProcessors / 2, SECONDS)
                                .plus(Duration.of(allProcessors / 2 * 100, MILLIS)))));
    }

    @Test
    public void with_futures__fixed__all_processors__with_executor() {

        // when
        final Instant start = clock.instant();

        final List<String> prices =
                shopService.findPricesFixed(shopsAllProcessors, "my favorite product", ONE_SECOND, executor);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));

        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }
}

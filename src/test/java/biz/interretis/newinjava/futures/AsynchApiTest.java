package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_AND_A_TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TWO_AND_A_FIFTN_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TWO_SECONDS;
import static com.google.common.collect.Lists.newArrayList;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class AsynchApiTest {

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
    public void with_futures() {

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
    public void with_futures__all_processors() {

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
    public void with_futures__all_processors__with_executor() {

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
}

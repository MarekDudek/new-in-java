package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.FOUR_AND_A_TWO_FIFTNS_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.FOUR_SECONDS;
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
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class PipleliningTest {

    private DiscountService discountService;
    private ShopService shopService;

    private List<Shop> shopsHalfOfProcessors;
    private int halfOfProcessors;

    private List<Shop> shopsAllProcessors;
    private int allProcessors;

    private List<Shop> shopsTwiceTheProcessors;
    private int twiceTheProcessors;

    private Clock clock;

    @Before
    public void setup() {

        // given

        discountService = new DiscountService();
        shopService = new ShopService();

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

        shopsTwiceTheProcessors = newArrayList();
        for (int i = 0; i < availableProcessors * 2; i++) {
            shopsTwiceTheProcessors.add(new Shop("Shop #" + i));
        }
        twiceTheProcessors = shopsTwiceTheProcessors.size();

        clock = systemUTC();
    }

    @Test
    public void discount_calculation() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopsHalfOfProcessors
                .stream()
                .map(shop -> shop.getPriceAndDiscountCode("my favorite product", ONE_SECOND))
                .map(Quote::parse)
                .map(quote -> discountService.applyDiscount(quote, ONE_SECOND))
                .collect(Collectors.toList());

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));
        assertThat(
                between(start, valuesRetrieved),
                both(
                        greaterThanOrEqualTo(Duration.of(2 * halfOfProcessors, SECONDS))).and(
                        lessThan(Duration.of(2 * halfOfProcessors, SECONDS)
                                .plus(Duration.of(2 * halfOfProcessors * 100, MILLIS)))));
    }

    @Test
    public void discounted_price__serial() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findDiscountedPricesSerial(shopsHalfOfProcessors, "my favorite product", ONE_SECOND, ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));
        assertThat(
                between(start, valuesRetrieved),
                both(
                        greaterThanOrEqualTo(Duration.of(2 * halfOfProcessors, SECONDS))).and(
                        lessThan(Duration.of(2 * halfOfProcessors, SECONDS)
                                .plus(Duration.of(2 * halfOfProcessors * 100, MILLIS)))));
    }

    @Test
    public void discounted_price__parallel() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findDiscountedPricesParallel(shopsHalfOfProcessors, "my favorite product", ONE_SECOND, ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(halfOfProcessors));
        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(TWO_SECONDS)).and(lessThan(TWO_AND_A_FIFTN_OF_SECOND)));
    }

    @Test
    public void discounted_price__parallel__all_processors() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findDiscountedPricesParallel(shopsAllProcessors, "my favorite product", ONE_SECOND, ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(allProcessors));
        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(TWO_SECONDS)).and(lessThan(TWO_AND_A_FIFTN_OF_SECOND)));
    }

    @Test
    public void discounted_price__parallel__twice_the_processors() {

        // when
        final Instant start = clock.instant();

        final List<String> prices = shopService.findDiscountedPricesParallel(shopsTwiceTheProcessors, "my favorite product", ONE_SECOND, ONE_SECOND);

        final Instant valuesRetrieved = clock.instant();

        // then
        assertThat(prices, hasSize(twiceTheProcessors));
        assertThat(
                between(start, valuesRetrieved),
                both(greaterThanOrEqualTo(FOUR_SECONDS)).and(lessThan(FOUR_AND_A_TWO_FIFTNS_OF_SECOND)));
    }
}

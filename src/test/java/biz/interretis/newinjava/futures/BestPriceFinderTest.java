package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_AND_A_TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.Future;

import org.junit.Test;

public class BestPriceFinderTest {

    @Test
    public void asynchronous_api_invocation() throws Exception {

        // given
        final Shop shop = new Shop("BestShop");
        final Clock clock = systemUTC();

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
        assertThat(between(start, valueRetrieved), both(greaterThan(ONE_SECOND)).and(lessThan(ONE_AND_A_TENTH_OF_SECOND)));
    }
}

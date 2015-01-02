package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Duration;
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

        final Future<Double> price = shop.getPriceAsynch("my favorite product");

        final Instant invocationReturned = clock.instant();

        doSomethingElse(Duration.ofSeconds(1).toMillis());

        // then
        assertThat(between(start, invocationReturned), lessThan(Duration.ofMillis(50)));

        // when
        price.get();

        final Instant valueRetrieved = clock.instant();

        // then
        assertThat(between(start, valueRetrieved), greaterThan(Duration.ofSeconds(1)));
    }
}

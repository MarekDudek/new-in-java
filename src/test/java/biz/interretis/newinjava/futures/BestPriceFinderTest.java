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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.hamcrest.Matchers;
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

    @Test(expected = TimeoutException.class)
    public void asynchronous_method_invocation__with_unhandled_error() throws Exception {

        // given
        final Shop shop = new Shop("BestShop");

        // when
        final Future<Double> price = shop.getPriceAsynch("p", TENTH_OF_SECOND);

        price.get(1, TimeUnit.SECONDS);

        // then exception is thrown
    }

    @Test
    public void asynchronous_method_invocation__with_error_handled() throws Exception {

        // given
        final Shop shop = new Shop("BestShop");

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
}

package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_AND_A_TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.TENTH_OF_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static java.time.Clock.systemUTC;
import static java.time.Duration.between;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

public class SingleInvocationTest {

    /** System under test. */
    private Shop shop;

    private Clock clock;

    @Before
    public void setup() {

        // given
        shop = new Shop("BestShop");

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

}

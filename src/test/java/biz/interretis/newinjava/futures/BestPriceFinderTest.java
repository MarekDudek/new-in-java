package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static java.lang.System.nanoTime;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Future;

import org.junit.Test;

public class BestPriceFinderTest {

    @Test
    public void asynchronous_api_invocation() throws Exception {

        // given
        final Shop shop = new Shop("BestShop");

        // when
        final long start = nanoTime();
        final Future<Double> futurePrice = shop.getPriceAsynch("my favorite product");
        final long invocationTime = ((nanoTime() - start)) / 1_000_000;

        doSomethingElse(1_000L);

        // then
        assertThat(invocationTime, lessThan(50L));

        // when
        futurePrice.get();
        final long retrievalTime = ((nanoTime() - start)) / 1_000_000;

        // then
        assertThat(retrievalTime, greaterThan(1000L));
    }
}

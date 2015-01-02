package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ANSWER_TO_ULTIMATE_QUESTION;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomeLongComputation;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class CompletableFuturesTest {

    @Test
    public void executing_long_lasting_operation_succeeds__pre_java_8() throws Exception {

        // given
        final ExecutorService executor = Executors.newCachedThreadPool();

        // when
        final Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() {
                return doSomeLongComputation(1000L);
            }
        });

        doSomethingElse(1100L);

        // then
        assertThat(future.isDone(), is(true));

        final Double answer = future.get(1, TimeUnit.MILLISECONDS);
        assertThat(answer, is(equalTo(ANSWER_TO_ULTIMATE_QUESTION)));
    }

    @Test(expected = TimeoutException.class)
    public void executing_long_lasting_operation_fails__pre_java_8() throws Exception {

        // given
        final ExecutorService executor = Executors.newCachedThreadPool();

        // when
        final Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() {
                return doSomeLongComputation(1000L);
            }
        });

        doSomethingElse(500L);

        // then
        assertThat(future.isDone(), is(false));

        future.get(1, TimeUnit.MILLISECONDS);
    }
}

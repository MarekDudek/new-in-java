package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.ANSWER_TO_ULTIMATE_QUESTION;
import static biz.interretis.newinjava.futures.ComputationsSimulator.ONE_SECOND;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomeLongComputation;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class CompletableFuturesTest {

    @Test
    public void executing_long_lasting_operation_succeeds__pre_java_8() throws Exception {

        // given
        final ExecutorService executor = newCachedThreadPool();

        // when
        final Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                return doSomeLongComputation(ONE_SECOND);
            }
        });

        doSomethingElse(Duration.ofMillis(1100));

        // then
        assertThat(future.isDone(), is(true));

        final Integer answer = future.get(1, TimeUnit.MILLISECONDS);
        assertThat(answer, is(equalTo(ANSWER_TO_ULTIMATE_QUESTION)));
    }

    @Test(expected = TimeoutException.class)
    public void executing_long_lasting_operation_fails__pre_java_8() throws Exception {

        // given
        final ExecutorService executor = newCachedThreadPool();

        // when
        final Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                return doSomeLongComputation(ONE_SECOND);
            }
        });

        doSomethingElse(Duration.ofMillis(500));

        // then
        assertThat(future.isDone(), is(false));

        future.get(1, TimeUnit.MILLISECONDS); // throws exception
    }

    @Test(expected = TimeoutException.class)
    public void executing_long_lasting_operation_fails__with_lambda() throws Exception {

        // given
        final ExecutorService executor = newCachedThreadPool();

        // when
        final Future<Integer> future = executor.submit(() -> doSomeLongComputation(ONE_SECOND));

        doSomethingElse(Duration.ofMillis(500));

        // then
        assertThat(future.isDone(), is(false));

        future.get(1, TimeUnit.MILLISECONDS);// throws exception
    }
}

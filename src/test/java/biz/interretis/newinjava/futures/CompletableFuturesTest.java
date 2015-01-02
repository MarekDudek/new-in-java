package biz.interretis.newinjava.futures;

import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomeLongComputation;
import static biz.interretis.newinjava.futures.ComputationsSimulator.doSomethingElse;

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

        doSomethingElse(1000L);

        // then
        future.get(1, TimeUnit.MILLISECONDS);
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
        future.get(1, TimeUnit.MILLISECONDS);
    }
}

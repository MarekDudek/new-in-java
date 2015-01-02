package biz.interretis.newinjava.futures;

import java.time.Duration;

public class ComputationsSimulator {

    public static final double ANSWER_TO_ULTIMATE_QUESTION = 42.0;

    static final long DEFAULT_DELAY = 1000L;

    public static Double doSomeLongComputation(final Duration duration) {

        delay(duration);

        return ANSWER_TO_ULTIMATE_QUESTION;
    }

    public static void doSomethingElse(final Duration duration) {

        delay(duration);
    }

    public static void delay() {

        delay(Duration.ofMillis(DEFAULT_DELAY));
    }

    private static void delay(final Duration duration)
    {
        try {
            Thread.sleep(duration.toMillis());
        } catch (final InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}

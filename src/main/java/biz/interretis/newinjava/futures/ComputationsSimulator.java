package biz.interretis.newinjava.futures;

import java.time.Duration;

public class ComputationsSimulator {

    public static final int ANSWER_TO_ULTIMATE_QUESTION = 42;

    static final long DEFAULT_DELAY = 1000L;
    public static final Duration ONE_SECOND = Duration.ofSeconds(1);

    public static Integer doSomeLongComputation(final Duration duration) {

        delay(duration);

        return ANSWER_TO_ULTIMATE_QUESTION;
    }

    public static void doSomethingElse(final Duration duration) {

        delay(duration);
    }

    public static void delay(final Duration duration)
    {
        try {
            Thread.sleep(duration.toMillis());
        } catch (final InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}

package biz.interretis.newinjava.futures;

import java.time.Duration;

public class ComputationsSimulator {

    public static final int ANSWER_TO_ULTIMATE_QUESTION = 42;

    public static final Duration ONE_SECOND = Duration.ofSeconds(1);
    public static final Duration HALF_A_SECOND = Duration.ofMillis(500);
    public static final Duration TENTH_OF_SECOND = Duration.ofMillis(100);

    public static final Duration ONE_AND_A_TENTH_OF_SECOND = ONE_SECOND.plus(TENTH_OF_SECOND);

    public static final Duration TWO_SECONDS = Duration.ofSeconds(2);
    public static final Duration TWO_AND_A_FIFTN_OF_SECOND = TWO_SECONDS.plus(TENTH_OF_SECOND).plus(TENTH_OF_SECOND);

    public static final Duration FOUR_SECONDS = Duration.ofSeconds(4);
    public static final Duration FOUR_AND_A_TWO_FIFTNS_OF_SECOND =
            FOUR_SECONDS.plus(TENTH_OF_SECOND).plus(TENTH_OF_SECOND).plus(TENTH_OF_SECOND).plus(TENTH_OF_SECOND);

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
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

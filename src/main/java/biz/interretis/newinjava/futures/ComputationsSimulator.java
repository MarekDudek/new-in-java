package biz.interretis.newinjava.futures;

public class ComputationsSimulator {

    public static final double ANSWER_TO_ULTIMATE_QUESTION = 42.0;

    static final long DEFAULT_DELAY = 1000L;

    public static Double doSomeLongComputation(final long millis) {

        delay(millis);

        return ANSWER_TO_ULTIMATE_QUESTION;
    }

    public static void doSomethingElse(final long millis) {

        delay(millis);
    }

    public static void delay() {

        delay(DEFAULT_DELAY);
    }

    public static void delay(final long millis) {

        try {
            Thread.sleep(millis);
        } catch (final InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}

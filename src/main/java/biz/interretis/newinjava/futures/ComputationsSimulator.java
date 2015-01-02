package biz.interretis.newinjava.futures;


public class ComputationsSimulator {

    public static final int ANSWER_TO_ULTIMATE_QUESTION = 42;

    public static Double doSomeLongComputation(final long millis) {

        delay(millis);

        return (double) ANSWER_TO_ULTIMATE_QUESTION;
    }

    public static void doSomethingElse(final long millis) {

        delay(millis);
    }

    public static void delay(final long millis) {

        try {
            Thread.sleep(millis);
        } catch (final InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}

package Main.services;

public class SleepService {

    public static synchronized void sleep() throws InterruptedException {
        Thread.sleep(500);
    }
}

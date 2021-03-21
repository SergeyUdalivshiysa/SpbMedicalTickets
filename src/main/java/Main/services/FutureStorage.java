package Main.services;

import Main.Main;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

public class FutureStorage {

    public static HashMap<Integer, ScheduledFuture> futureStorage = new HashMap<>();

    public static synchronized void putToStorage(int id, ScheduledFuture scheduledFuture) {
        futureStorage.put(id, scheduledFuture);
    }

    public static synchronized ScheduledFuture deleteFromStorage(int id) {
        return futureStorage.remove(id);
    }

    public static ScheduledFuture get(int id) {
        return futureStorage.get(id);
    }
}

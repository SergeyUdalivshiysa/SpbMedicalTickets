package Main.services;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class FutureStorage {

    public static ConcurrentHashMap<Integer, ScheduledFuture> futureStorage = new ConcurrentHashMap<>();

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

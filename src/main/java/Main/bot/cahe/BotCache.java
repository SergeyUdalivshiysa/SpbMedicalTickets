package Main.bot.cahe;

import Main.bot.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class BotCache {

    @Value("${bot.cacheCleanRateMinutes}")
    static int cleanRate;

    static private Map<Long, UserStatus> userStatusCache = new ConcurrentHashMap<>();

    static public void put(Long id, UserStatus userStatus) {
        userStatusCache.put(id, userStatus);
    }

    static public UserStatus getUserStatus(long id) {
        return userStatusCache.get(id);
    }

    static public void deleteEntry(long id) {
        userStatusCache.remove(id);
    }




    @Scheduled(fixedRate = 1000 * 60 * 30)
    static public void cleanCache() {
        userStatusCache.forEach((aLong, userStatus) -> {
            if (Instant.now().compareTo(userStatus.getLastInteractionTime().plus(cleanRate, ChronoUnit.MINUTES)) > 0) {
                userStatusCache.remove(aLong);
                System.out.println("удаляем " + aLong);
            }
        });

    }
}

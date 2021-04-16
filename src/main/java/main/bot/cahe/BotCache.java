package main.bot.cahe;

import main.bot.BotState;
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

    static public void putWithBotState(long id, BotState botState) {

    }

    static public UserStatus getUserStatus(long id) {
        return userStatusCache.get(id);
    }

    static public void deleteEntry(long id) {
        userStatusCache.remove(id);
    }

    static public BotState getBotState(long id) {
       UserStatus userStatus = userStatusCache.getOrDefault(id, null);
       if (userStatus != null) return userStatus.getBotState();
       else return BotState.NO_STATE;
    }

    static public void saveToCache(long id, BotState botState) {
        UserStatus userStatus = userStatusCache.getOrDefault(id, new UserStatus(botState));
        userStatus.setBotState(botState);
        BotCache.put(id, userStatus);
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

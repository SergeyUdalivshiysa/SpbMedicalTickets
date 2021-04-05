package Main.bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Scope("prototype")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserStatus {

    BotStatus botStatus;
    Instant lastInteractionTime;
    String url;
    String name;

    public UserStatus() {
        botStatus = BotStatus.INITIALIZED;
        lastInteractionTime = Instant.now();
    }

    public UserStatus(BotStatus botStatus) {
        this.botStatus = botStatus;
        lastInteractionTime = Instant.now();
    }

}

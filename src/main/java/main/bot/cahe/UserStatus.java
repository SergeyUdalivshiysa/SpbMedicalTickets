package main.bot.cahe;

import main.bot.BotState;
import main.entities.TaskEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;

@Component
@Scope("prototype")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserStatus {

    BotState botState;
    Instant lastInteractionTime;
    String url;
    String name;
    ArrayList<TaskEntity> taskEntities = null;

    public UserStatus() {
        botState = BotState.MENU;
        lastInteractionTime = Instant.now();
    }

    public UserStatus(BotState botStatus) {
        this.botState = botStatus;
        lastInteractionTime = Instant.now();
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
        lastInteractionTime = Instant.now();
    }

}

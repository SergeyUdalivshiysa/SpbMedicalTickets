package main.bot.statehandlers.newsearchcreation;

import main.bot.BotState;
import main.bot.cahe.BotCache;
import main.bot.cahe.UserStatus;
import main.bot.statehandlers.Handler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class InsertDoctorNameHandler implements Handler {
    @Override
    public SendMessage handleUpdate(Update update) {
        String messageText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        UserStatus userStatus = BotCache.getUserStatus(userId);

        userStatus.setBotState(BotState.INSERT_MINIMUM_TICKET_NUMBER_NEEDED);
        userStatus.setName(messageText);
        System.out.println(userStatus.getUrl() + "   " + userStatus.getName());
        return new SendMessage(String.valueOf(userId),
                "Введите число номерков, по появлению которых вам придет оповещение. Если номерков нет совсем, введите 0.");
    }
}

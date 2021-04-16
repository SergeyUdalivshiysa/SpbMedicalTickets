package main.bot.statehandlers.newsearchcreation;

import main.bot.BotState;
import main.bot.cahe.BotCache;
import main.bot.cahe.UserStatus;
import main.bot.statehandlers.Handler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class InsertLinkHandler implements Handler {
    @Override
    public SendMessage handleUpdate(Update update) {
        String messageText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        UserStatus userStatus = BotCache.getUserStatus(userId);

        if (isUrlCorrect(messageText)) {
            messageText = ensureUrlCompleteness(messageText);
            userStatus.setBotState(BotState.INSERT_DOCTOR_NAME);
            userStatus.setUrl(messageText);
            return new SendMessage(String.valueOf(userId),
                    "Введите доктора");
        }
        else return handleIncorrectUrl(userId);
    }

    private boolean isUrlCorrect(String url) {
        return url.matches(
                "^(https://gorzdrav.spb.ru/service-free-schedule#).+|^(gorzdrav.spb.ru/service-free-schedule#).+");
    }

    private String ensureUrlCompleteness (String url) {
        if (url.matches("^(gorzdrav.spb.ru/service-free-schedule#).+")) {
            return "https://" + url;
        }
        else return url;
    }

    private SendMessage handleIncorrectUrl(long id) {
        return new SendMessage(String.valueOf(id),
                "Введенный url не верен, введите корректный url, с сайта gorzdrav.spb.ru");
    }
}

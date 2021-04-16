package main.bot.statehandlers.newsearchcreation;

import main.bot.BotState;
import main.bot.cahe.BotCache;
import main.bot.statehandlers.Handler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NewSearchHandler implements Handler {
    @Override
    public SendMessage handleUpdate(Update update) {
        long userId = update.getMessage().getFrom().getId();
        BotCache.saveToCache(userId, BotState.INSERT_LINK);
        return new SendMessage(String.valueOf(userId),
                "Введите ссылку на сайте");
    }
}

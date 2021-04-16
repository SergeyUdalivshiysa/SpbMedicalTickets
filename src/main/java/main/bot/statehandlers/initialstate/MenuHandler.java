package main.bot.statehandlers.initialstate;

import main.bot.statehandlers.Handler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MenuHandler implements Handler {
    @Override
    public SendMessage handleUpdate(Update update) {
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId),
                "Введите: '/find' для запуска нового поиска номерков.\nВведите /myrequests для просмотра и удалении существующих запросов.");
    }
}

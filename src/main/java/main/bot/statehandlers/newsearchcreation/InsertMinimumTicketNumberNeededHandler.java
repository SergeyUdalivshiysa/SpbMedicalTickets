package main.bot.statehandlers.newsearchcreation;

import main.bot.BotState;
import main.bot.cahe.BotCache;
import main.bot.cahe.UserStatus;
import main.bot.statehandlers.Handler;
import main.dto.LookUpTaskDTO;
import main.services.LookUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class InsertMinimumTicketNumberNeededHandler implements Handler {
    @Autowired
    LookUpService lookUpService;

    @Override
    public SendMessage handleUpdate(Update update) {
        String messageText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        UserStatus userStatus = BotCache.getUserStatus(userId);
        long chatId = update.getMessage().getChatId();

        if (!messageText.matches("\\d+")) {
            return new SendMessage(String.valueOf(userId),
                    "Введено некорректное значение, введите число");
        } else {
            try {
                int minimumTicketNumberNeeded = Integer.parseInt(messageText);
                userStatus.setBotState(BotState.MENU);
                if (minimumTicketNumberNeeded == 0) minimumTicketNumberNeeded = 1;
                String url = userStatus.getUrl();
                String name = userStatus.getName();
                LookUpTaskDTO lookUpTaskDTO = new LookUpTaskDTO(url, name, userId, chatId, minimumTicketNumberNeeded);
                lookUpService.crateScheduledLookUpTask(lookUpTaskDTO);
                return new SendMessage(String.valueOf(userId), String.format(
                        "Запрос создан:\nИмя доктора/Процедуры/Кабинета - %s\nurl - %s", name, url));
            } catch (NumberFormatException e) {
                return new SendMessage(String.valueOf(userId),
                        "Введено некорректное значение, введите корректное");
            }
        }
    }
}

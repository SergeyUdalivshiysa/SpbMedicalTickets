package main.bot.statehandlers.newsearchcreation;

import main.bot.statehandlers.Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TooMuchRequestsForUserHandler implements Handler {

    @Value("${maximumAmountOfTasksPerUser}")
    int maximumAmountOfTasksPerUser;

    @Override
    public SendMessage handleUpdate(Update update) {
        long userId = update.getMessage().getFrom().getId();
        return new SendMessage(String.valueOf(userId),
                String.format("У вас уже выполняется максимальное количество запросов на одного пользователя, которое равняется %s. Для добавления нового запроса удалите старый.\n\nДля просмотра существующий запросов введите /myrequests", maximumAmountOfTasksPerUser));
    }
}

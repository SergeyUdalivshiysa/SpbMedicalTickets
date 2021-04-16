package main.bot.statehandlers.requestsmgmt;

import main.bot.BotState;
import main.bot.cahe.BotCache;
import main.bot.cahe.UserStatus;
import main.bot.statehandlers.Handler;
import main.entities.TaskEntity;
import main.services.LookUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Component
public class DeleteHandler implements Handler {
    @Autowired
    LookUpService lookUpService;

    @Override
    public SendMessage handleUpdate(Update update) {
        String messageText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        UserStatus userStatus = BotCache.getUserStatus(userId);

        if (!messageText.matches("\\d+")) {
            return new SendMessage(String.valueOf(userId),
                    "Введено некорректное значение, введите число");
        }
        ArrayList<TaskEntity> taskEntities = userStatus.getTaskEntities();
        if (taskEntities == null || taskEntities.size() == 0) {
            userStatus.setBotState(BotState.MENU);
            return new SendMessage(String.valueOf(userId), "На вашем аккаунте отсутствуют задачи.\nВведите /find для нового поиска.");
        }
        try {
            int taskNumber = Integer.parseInt(messageText);
            TaskEntity taskEntity;
            taskEntity = taskEntities.get(taskNumber - 1);
            lookUpService.eliminateTask(taskEntity.getId());
            return new SendMessage(String.valueOf(userId), "Задача удалена.\nДля удаления другой задачи введите ее номер.\nДля возвращения в главное меню введите /menu");
        }
        catch (IndexOutOfBoundsException | NumberFormatException e) {
            return new SendMessage(String.valueOf(userId),
                    "Введено некорректное значение, введите корректное");
        }
    }
}

package main.bot.statehandlers.requestsmgmt;

import main.bot.BotState;
import main.bot.cahe.BotCache;
import main.bot.cahe.UserStatus;
import main.bot.statehandlers.Handler;
import main.entities.TaskEntity;
import main.repository.TicketEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Component
public class MyRequestsHandler implements Handler {
    @Autowired
    TicketEntityRepository ticketEntityRepository;

    @Override
    public SendMessage handleUpdate(Update update) {
        long userId = update.getMessage().getFrom().getId();
        UserStatus userStatus = BotCache.getUserStatus(userId);
        String messageText = update.getMessage().getText();

        if (messageText.equals("/delete")) {
            userStatus.setBotState(BotState.DELETE);
            return new SendMessage(String.valueOf(userId), "Введите номер задание, которое необходимо удалить.\nВведите /menu для возврата в главное меню");
        }
        else return getUserRequests(userId, userStatus);
    }

    private SendMessage getUserRequests(long userId, UserStatus userStatus) {

        ArrayList<TaskEntity> requests = (ArrayList) ticketEntityRepository.findUserRequests(userId);
        if (requests == null || requests.size() == 0) {
            userStatus.setBotState(BotState.MENU);
            return new SendMessage(String.valueOf(userId), "Запросов нет.\nВведите /find для создания нового запроса");

        } else {
            userStatus.setTaskEntities(requests);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < requests.size(); i++) {
                TaskEntity taskEntity = requests.get(i);
                stringBuilder
                        .append("Запрос ")
                        .append(i + 1)
                        .append(":\n url - ")
                        .append(taskEntity.getUrl())
                        .append("\n имя - ")
                        .append(taskEntity.getDoctorOrCabinetName()).append("\n\n");
            }
            stringBuilder.append("Введите /delete для удаления запроса\nВведите /menu для возврата в главное меню.");
            return new SendMessage(String.valueOf(userId), stringBuilder.toString());
        }

    }
}

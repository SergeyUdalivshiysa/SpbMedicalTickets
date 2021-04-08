package Main.bot;

import Main.bot.cahe.BotCache;
import Main.dto.LookUpTaskDTO;
import Main.entities.TaskEntity;
import Main.repository.TicketEntityRepository;
import Main.services.LookUpService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.ArrayList;
import java.util.List;


//класс выполняет весь функционал, но требует рефакторинга
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateHandler {
    Update update;
    SendMessage sendMessage;
    Message message;
    long userId;
    long chatId;
    String messageText;
    UserStatus userStatus;

    @Value("${maximumAmountOfTasksPerUser}")
    int maximumAmountOfTasksPerUser;

    @Autowired
    LookUpService lookUpService;

    @Autowired
    TicketEntityRepository ticketEntityRepository;

    public SendMessage handleUpdate(Update update) {
        this.update = update;
        message = update.getMessage();
        userId = message.getFrom().getId();
        chatId = message.getChatId();
        messageText = message.getText();
        return handleMessageText(update);
    }

    private SendMessage handleMessageText(Update update) {
        switch (messageText) {

            case "/start":
                saveToCache(BotState.MENU);
                return sendMessage = new SendMessage(String.valueOf(chatId),
                        "Введите: '/find' для запуска нового поиска номерков.\nВведите /myrequests для просмотра и удалении существующих запросов.");

            case "/find":
                if (getNumberOfExistingTasksForUser(userId) >= maximumAmountOfTasksPerUser) {
                    return sendMessage = new SendMessage(String.valueOf(chatId),
                            String.format("У вас уже выполняется максимальное количество запросов на одного пользователя, которое равняется %s. Для добавления нового запроса удалите старый", maximumAmountOfTasksPerUser));
                }
                else {
                    saveToCache(BotState.INSERT_LINK);
                    return sendMessage = new SendMessage(String.valueOf(chatId),
                            "Введите ссылку на сайте");
                }

            case "/myrequests":
                saveToCache(BotState.MY_REQUESTS);
                return getUserRequests(userId);
            default:
                return handleUnrecognizedMessage(update);
        }
    }




    private SendMessage handleUnrecognizedMessage(Update update) {
        BotState botState;
        if (BotCache.getUserStatus(userId) == null) {
            botState = null;
        } else {
            botState = BotCache.getUserStatus(userId).getBotState();
        }
        if (botState == null) {
            return sendMessage = new SendMessage(String.valueOf(chatId),
                    "Введите /start для начала");
        } else {
            UserStatus userStatus = BotCache.getUserStatus(userId);
            switch (botState) {

                case INSERT_LINK:
                    if (isUrlCorrect(messageText)) {
                        messageText = ensureUrlCompleteness(messageText);
                        userStatus.setBotState(BotState.INSERT_DOCTOR_NAME);
                        userStatus.setUrl(messageText);
                        return sendMessage = new SendMessage(String.valueOf(chatId),
                                "Введите доктора");
                    }
                    else return handleIncorrectUrl();

                case INSERT_DOCTOR_NAME:
                    userStatus.setBotState(BotState.INSERT_MINIMUM_TICKET_NUMBER_NEEDED);
                    userStatus.setName(messageText);
                    return sendMessage = new SendMessage(String.valueOf(chatId),
                            "Введите число номерков, по появлению которых вам придет оповещение. Если номерков нет совсем, введите 0.");

                case INSERT_MINIMUM_TICKET_NUMBER_NEEDED:
                    if (!messageText.matches("\\d+")) {
                        return sendMessage = new SendMessage(String.valueOf(chatId),
                                "Введено некорректное значение, введите число");
                    }
                    else {
                        try {
                            int minimumTicketNumberNeeded = Integer.parseInt(messageText);
                            userStatus.setBotState(BotState.QUERY_CREATED);
                            if (minimumTicketNumberNeeded == 0) minimumTicketNumberNeeded = 1;
                            String url = userStatus.getUrl();
                            String name = userStatus.getName();
                            LookUpTaskDTO lookUpTaskDTO = new LookUpTaskDTO(url, name, userId, chatId, minimumTicketNumberNeeded);
                            lookUpService.crateScheduledLookUpTask(lookUpTaskDTO);
                            return sendMessage = new SendMessage(String.valueOf(chatId), String.format(
                                    "Запрос создан:\nИмя доктора/Процедуры/Кабинета - %s\nurl - %s", name, url));
                        }
                        catch (NumberFormatException e) {
                            return sendMessage = new SendMessage(String.valueOf(chatId),
                                    "Введено некорректное значение, введите корректное");
                        }

                    }
                case MY_REQUESTS:
                    if (messageText.equals("/delete")) {
                        userStatus.setBotState(BotState.DELETE);
                        return sendMessage = new SendMessage(String.valueOf(chatId), "Введите номер задание, которое необходимо удалить.\nВведите /menu для возврата в главное меню");
                    }

                case DELETE:
                    if (!messageText.matches("\\d+")) {
                        return sendMessage = new SendMessage(String.valueOf(chatId),
                                "Введено некорректное значение, введите число");
                    }
                    ArrayList<TaskEntity> taskEntities = userStatus.getTaskEntities();
                    if (taskEntities == null || taskEntities.size() == 0) {
                        userStatus.setBotState(BotState.MENU);
                        return sendMessage = new SendMessage(String.valueOf(chatId), "На вашем аккаунте отсутствуют задачи.\nВведите /find для нового поиска.");
                    }
                    try {
                        int taskNumber = Integer.parseInt(messageText);
                        TaskEntity taskEntity;
                        taskEntity = taskEntities.get(taskNumber - 1);
                        lookUpService.eliminateTask(taskEntity.getId());
                        return sendMessage = new SendMessage(String.valueOf(chatId), "Задача удалена.\n Для удаления другой задачи введите ее номер.\nДля возвращения в главное меню введите /menu");
                    }
                    catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        return sendMessage = new SendMessage(String.valueOf(chatId),
                                "Введено некорректное значение, введите корректное");
                    }
                default:
                    return sendMessage = new SendMessage(String.valueOf(chatId),
                            "Введите: '/find' для запуска нового поиска номерков");
            }
        }
    }

    private boolean isUrlCorrect(String url) {
        return url.matches(
                "^(https://gorzdrav.spb.ru/service-free-schedule#).+|^(gorzdrav.spb.ru/service-free-schedule#).+");
    }


    private String ensureUrlCompleteness (String url) {
        if (url.matches("^(gorzdrav.spb.ru/service-free-schedule#).+")) {
            return "https://" +url;
        }
        else return url;
    }

    private SendMessage handleIncorrectUrl() {
        return sendMessage = new SendMessage(String.valueOf(chatId),
                "Введенный url не верен, введите корректный url, с сайта gorzdrav.spb.ru");
    }

    private SendMessage getUserRequests(long userId) {
        ArrayList<TaskEntity> requests = (ArrayList) ticketEntityRepository.findUserRequests(userId);
        if (requests.size() == 0) {
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
            return new SendMessage(String.valueOf(chatId), stringBuilder.toString());
        }
    }


    private void saveToCache(BotState botState) {
        userStatus = new UserStatus(botState);
        BotCache.put(userId, userStatus);
    }

    private int getNumberOfExistingTasksForUser(long userId) {
        List tasks = (List) ticketEntityRepository.findUserRequests(userId);
        if (tasks == null) return 0;
        else return tasks.size();
    }
}

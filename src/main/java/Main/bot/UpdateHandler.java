package Main.bot;
import Main.bot.cahe.BotCache;
import Main.dto.LookUpTaskDTO;
import Main.services.LookUpService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UpdateHandler {
    Update update;
    SendMessage sendMessage;
    Message message;
    long userId;
    long chatId;
    String messageText;
    UserStatus userStatus;

    @Autowired
    private LookUpService lookUpService;

public SendMessage handleUpdate(Update update) {
    this.update = update;
    message = update.getMessage();
    userId = message.getFrom().getId();
    chatId = message.getChatId();
    messageText = message.getText();
    return handleMessageText(update);
}

    public SendMessage handleMessageText(Update update) {
        switch (messageText) {
            case "/start":
                userStatus = new UserStatus();
                BotCache.put(userId, userStatus);
                return sendMessage = new SendMessage(String.valueOf(chatId), "Введите: \'/найти\' для запуска нового поиса номерков");
            case "/найти":
                userStatus = new UserStatus(BotStatus.QUERY_CREATED);
                BotCache.put(userId, userStatus);
                return sendMessage = new SendMessage(String.valueOf(chatId), "Введите ссылку на сайте");
            default:
                return handleUnrecognizedMessage(update);
        }
    }


    private SendMessage handleUnrecognizedMessage(Update update) {
        BotStatus botStatus;
        if (BotCache.getUserStatus(userId) == null) {
            botStatus = null;
        } else {
            botStatus = BotCache.getUserStatus(userId).getBotStatus();
        }
        if (botStatus == null) {
            return sendMessage = new SendMessage(String.valueOf(chatId), "Введите /start для начала");
        } else {
            UserStatus userStatus = BotCache.getUserStatus(userId);
            switch (botStatus) {
                case QUERY_CREATED:
                    userStatus = new UserStatus(BotStatus.INSERT_LINK);
                    BotCache.put(userId, userStatus);
                    return sendMessage = new SendMessage(String.valueOf(chatId), "");
                case INSERT_LINK:
                    userStatus.setBotStatus(BotStatus.INSERT_DOCTOR_NAME);
                    userStatus.setUrl(messageText);
                    return sendMessage = new SendMessage(String.valueOf(chatId), "Введите доктора");
                case INSERT_DOCTOR_NAME:
                    userStatus.setBotStatus(BotStatus.QUERY_CREATED);
                    userStatus.setName(messageText);
                    String url = userStatus.getUrl();
                    String name = userStatus.getName();
                    LookUpTaskDTO lookUpTaskDTO = new LookUpTaskDTO(url, name, userId, chatId);
                    lookUpService.crateScheduledLookUpTask(lookUpTaskDTO);
                    return sendMessage = new SendMessage(String.valueOf(chatId), String.format("Запрос создан: Имя доктора/Процедуры/Кабинета - %s, url - %s", name, url));
                default:
                    return sendMessage = new SendMessage(String.valueOf(chatId), "Введите: \'/найти\' для запуска нового поиса номерков");
            }
        }
    }
}

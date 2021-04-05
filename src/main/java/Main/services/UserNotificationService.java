package Main.services;

import Main.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class UserNotificationService {

    @Autowired
    Bot bot;

    public SendMessage sendMessageToUser(String message, long chatId) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return sendMessage;
    }
}

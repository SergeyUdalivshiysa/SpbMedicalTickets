package main.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends TelegramWebhookBot {

    @Value("${bot.username}")
    String username;
    @Value("${bot.token}")
    String token;
    @Value("${bot.path}")
    String botPath;

    @Autowired
    UpdateHandler updateHandler;


    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage sendMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            sendMessage = updateHandler.handleUpdate(update);
        }
        return sendMessage;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }



}

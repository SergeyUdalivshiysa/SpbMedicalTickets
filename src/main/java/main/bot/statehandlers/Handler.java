package main.bot.statehandlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {

    SendMessage handleUpdate(Update update);
}

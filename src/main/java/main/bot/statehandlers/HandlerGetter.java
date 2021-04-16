package main.bot.statehandlers;

import main.bot.BotState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class HandlerGetter {

    @Autowired
    ApplicationContext context;

    public Handler getHandler(BotState botState) {
        if (botState == null) {
            return (Handler) context.getBean("noStateHandler");
        }
        return (Handler) context.getBean(getHandlerName(botState));
    }

    private String getHandlerName(BotState botState) {
        String botStateName = botState.toString().toLowerCase();
        StringBuilder stringBuilder = new StringBuilder(botStateName);
        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '_') {
                stringBuilder.deleteCharAt(i);
                char ch = stringBuilder.charAt(i);
                stringBuilder.replace(i, i+1, String.valueOf(Character.toUpperCase(ch)));
            }
        }
        stringBuilder.append("Handler");
        return stringBuilder.toString();
    }

}

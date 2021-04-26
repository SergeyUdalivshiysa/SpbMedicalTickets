package main.bot.statehandlers;

import main.bot.BotState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

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
        String[] strings = botStateName.split("_");
        Optional<String> output = Arrays.stream(strings)
                .map(word -> word.equals(strings[0]) ? word : makeFirstLetterUpperCase(word))
                .reduce((x, y) -> x + y);
        return  output.get() + "Handler";
    }

    private static String makeFirstLetterUpperCase(String input) {
        char ch = input.charAt(0);
        return String.valueOf(ch).toUpperCase() + input.substring(1);
    }

/*    private String getHandlerName(BotState botState) {
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
    }*/

}

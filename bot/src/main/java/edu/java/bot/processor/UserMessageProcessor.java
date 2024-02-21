package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import org.springframework.stereotype.Component;

@Component
public class UserMessageProcessor implements IUserMessageProcessor {

    private final CommandHolder commandHolder;

    private static final String WRONG_COMMAND = "Wrong command! Please try again.";

    private static final String PATTERN = "\\s+";

    public UserMessageProcessor(CommandHolder commandHolder) {
        this.commandHolder = commandHolder;
    }

    @Override
    public SendMessage process(Update update) {
        String commandName = update.message().text();
        Command command;

        if (isSingleWord(commandName)) {
            command = commandHolder.getCommand(commandName);
        } else {
            command = commandHolder.getCommand(getFirstWord(commandName));
        }

        if (command == null) {
            return new SendMessage(
                update.message().chat().id(),
                WRONG_COMMAND
            );
        }

        return command.handle(update);
    }

    private boolean isSingleWord(String text) {
        return text.trim().split(PATTERN).length == 1;
    }

    private String getFirstWord(String text) {
        return text.trim().split(PATTERN)[0];
    }
}

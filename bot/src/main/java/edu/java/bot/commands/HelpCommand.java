package edu.java.bot.commands;

import java.util.List;
import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;



@Component
public class HelpCommand implements Command {

    private final List<Command> commandList;

    private static final String WRONG_COMMAND = "The command was entered incorrectly!"
        + " Type /help to view the available commands";

    public HelpCommand(List<Command> commandList) {
        this.commandList = commandList;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "provides a list of available commands";
    }

    @Override
    public SendMessage handle(Update update) {
        if (supports(update)) {
            StringBuilder answer = new StringBuilder("Available commands:\n");

            for (var command : commandList) {
                answer.append(command.command())
                    .append(" - ")
                    .append(command.description())
                    .append("\n");

            }

            return new SendMessage(
                update.message().chat().id(),
                answer.toString()
            );
        }
        return new SendMessage(update.message().chat().id(), WRONG_COMMAND);
    }

    @Override
    public boolean supports(Update update) {
        return update.message().text().equals(command());
    }
}

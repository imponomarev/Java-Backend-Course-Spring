package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.processor.CommandHolder;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Bot implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final UserMessageProcessor processor;
    private final CommandHolder commandHolder;


    public Bot(TelegramBot telegramBot, UserMessageProcessor processor, CommandHolder commandHolder) {
        this.telegramBot = telegramBot;
        this.processor = processor;
        this.commandHolder = commandHolder;
        telegramBot.execute(createMenu());
        telegramBot.setUpdatesListener(this);
    }

    private SetMyCommands createMenu() {
        List<BotCommand> botCommands = new ArrayList<>();
        for (Command command : commandHolder.getCommands()) {
            botCommands.add(new BotCommand(command.command(), command.description()));
        }
        return new SetMyCommands(botCommands.toArray(new BotCommand[0]));
    }

    @Override
    public int process(List<Update> updateList) {
        for (var update : updateList) {
            SendMessage message = processor.process(update);
            telegramBot.execute(message);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}

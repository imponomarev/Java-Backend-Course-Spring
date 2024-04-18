package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final ScrapperClient scrapperClient;

    private static final String ALREADY_REGISTERED = "You are already registered";
    private static final String SUCCESSFULLY_REGISTERED = "You have successfully registered";
    private static final String WRONG_COMMAND = "Enter /start to register in the bot";


    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "register a user";
    }

    @Override
    public SendMessage handle(Update update) {
        if (supports(update)) {
            try {
                scrapperClient.registerChat(update.message().chat().id());
            } catch (Exception e) {
                return new SendMessage(update.message().chat().id(), ALREADY_REGISTERED);
            }
            return new SendMessage(update.message().chat().id(), SUCCESSFULLY_REGISTERED);
        }
        return new SendMessage(update.message().chat().id(), WRONG_COMMAND);
    }

    @Override
    public boolean supports(Update update) {
        return update.message().text().equals(command());
    }
}

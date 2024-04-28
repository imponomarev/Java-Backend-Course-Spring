package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.RemoveLinkRequest;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackCommand implements Command {

    private final ScrapperClient scrapperClient;
    private static final String SUCCESSFULLY_UNSUBSCRIBED = "you have successfully unsubscribed from the resource";

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "stop tracking the link";
    }

    @Override
    public SendMessage handle(Update update) {
        String[] cmdAndUrl = update.message().text().split(" ");

        if (supports(update) && cmdAndUrl.length == 2) {
            String enteredUrl = cmdAndUrl[1];
            String scheme = "https://";
            String uri;

            if (!enteredUrl.startsWith(scheme)) {
                uri = scheme + enteredUrl;
            } else {
                uri = enteredUrl;
            }

            URI link = URI.create(uri);

            try {

                scrapperClient.retryRemoveLink(update.message().chat().id(), new RemoveLinkRequest(link));
                return new SendMessage(
                    update.message().chat().id(),
                    SUCCESSFULLY_UNSUBSCRIBED
                );

            } catch (ApiErrorException e) {
                return new SendMessage(
                    update.message().chat().id(),
                    e.getMessage()
                );
            }
        }
        return new SendMessage(
            update.message().chat().id(),
            "type /untrack + URL of the resource you want to unsubscribe from"
        );
    }

    @Override
    public boolean supports(Update update) {
        return update.message().text().split(" ")[0].equals(command());
    }
}

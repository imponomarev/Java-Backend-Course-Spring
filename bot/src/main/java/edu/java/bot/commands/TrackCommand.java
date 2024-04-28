package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.AddLinkRequest;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorException;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {

    private final ScrapperClient scrapperClient;
    private static final String SUCCESSFULLY_SUBSCRIBED = "you have successfully subscribed to the resource";
    private static final String PATTERN = "\\s+";

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "start tracking link";
    }

    @Override
    public SendMessage handle(Update update) {

        String[] cmdAndUrl = update.message().text().trim().split(PATTERN);

        if (supports(update) && cmdAndUrl.length == 2) {
            String enteredUrl = cmdAndUrl[1];
            String scheme = "https://";
            String uriStr = enteredUrl.startsWith(scheme) ? enteredUrl : scheme + enteredUrl;

            try {
                URI uri = URI.create(uriStr);
                Optional<LinkResponse> response =
                    scrapperClient.retryAddLink(update.message().chat().id(), new AddLinkRequest(uri));
                if (response.isPresent()) {
                    return new SendMessage(update.message().chat().id(), SUCCESSFULLY_SUBSCRIBED);
                } else {
                    return new SendMessage(update.message().chat().id(), "Failed to subscribe due to an error.");
                }
            } catch (ApiErrorException e) {
                return new SendMessage(update.message().chat().id(), e.getMessage());
            }
        }
        return new SendMessage(
            update.message().chat().id(),
            "type /track + url of the resource you want to subscribe to"
        );
    }

    @Override
    public boolean supports(Update update) {
        return update.message().text().split(" ")[0].equals(command());
    }

}

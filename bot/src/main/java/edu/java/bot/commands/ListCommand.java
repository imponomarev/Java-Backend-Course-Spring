package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.client.ScrapperClient;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {

    private final ScrapperClient scrapperClient;
    private static final String WITHOUT_SUBS = "You aren't subscribed to anything";
    private static final String WRONG_COMMAND = "The command was entered incorrectly!"
        + " Type /list to view your subscriptions";

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "show a list of tracked links";
    }

    @Override
    public SendMessage handle(Update update) {
        if (supports(update)) {
            try {
                List<URI> links = scrapperClient.getLinks(update.message().chat().id())
                    .get()
                    .links()
                    .stream()
                    .map(LinkResponse::url)
                    .toList();

                if (links.isEmpty()) {
                    return new SendMessage(
                        update.message().chat().id(), WITHOUT_SUBS
                    );
                }

                StringBuilder strLinks = new StringBuilder("your subscriptions:\n");

                for (var link : links) {
                    strLinks.append(link.toString()).append("\n");
                }
                return new SendMessage(
                    update.message().chat().id(),
                    strLinks.toString()
                );

            } catch (Exception e) {
                return new SendMessage(
                    update.message().chat().id(),
                    e.getMessage()
                );
            }
        }
        return new SendMessage(
            update.message().chat().id(),
            WRONG_COMMAND
        );
    }

    @Override
    public boolean supports(Update update) {
        return update.message().text().equals(command());
    }

}

package edu.java.bot.commandTests;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.api.model.RemoveLinkRequest;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.commands.UntrackCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private Chat chat;

    private UntrackCommand untrackCommand;

    @BeforeEach
    void setUp() {
        untrackCommand = new UntrackCommand(scrapperClient);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/untrack https://example.com");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
    }
    @Test
    void shouldUnsubscribeSuccessfully() {
        when(scrapperClient.removeLink(eq(123L), any(RemoveLinkRequest.class))).thenReturn(Optional.of(new LinkResponse(
            1L,
            URI.create("https://example.com"))));

        SendMessage response = untrackCommand.handle(update);

        assertEquals("you have successfully unsubscribed from the resource", response.getParameters().get("text"));
    }


    @Test
    void shouldHandleIncorrectCommand() {
        when(message.text()).thenReturn("/untrack");

        SendMessage response = untrackCommand.handle(update);

        assertEquals("type /untrack + URL of the resource you want to unsubscribe from", response.getParameters().get("text"));
    }
}

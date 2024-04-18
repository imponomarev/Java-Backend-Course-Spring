package edu.java.bot.commandTests;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.AddLinkRequest;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.commands.TrackCommand;
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
public class TrackCommandTest {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private Chat chat;

    private TrackCommand trackCommand;

    @BeforeEach
    void setUp() {
        trackCommand = new TrackCommand(scrapperClient);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/track https://example.com");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
    }

    @Test
    void shouldSubscribeSuccessfully() {
        when(scrapperClient.addLink(eq(123L), any(AddLinkRequest.class))).thenReturn(Optional.of(new LinkResponse(1L, URI.create("https://example.com"))));

        SendMessage response = trackCommand.handle(update);

        assertEquals("you have successfully subscribed to the resource", response.getParameters().get("text"));
    }

    @Test
    void shouldHandleFailureToSubscribe() {
        when(scrapperClient.addLink(eq(123L), any(AddLinkRequest.class))).thenReturn(Optional.empty());

        SendMessage response = trackCommand.handle(update);

        assertEquals("Failed to subscribe due to an error.", response.getParameters().get("text"));
    }

    @Test
    void shouldHandleIncorrectCommand() {
        when(message.text()).thenReturn("/track");

        SendMessage response = trackCommand.handle(update);

        assertEquals("type /track + url of the resource you want to subscribe to", response.getParameters().get("text"));
    }
}

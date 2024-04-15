package edu.java.bot.commandTests;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.api.model.ListLinksResponse;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.commands.ListCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private Chat chat;

    private ListCommand listCommand;

    @BeforeEach
    void setUp() {
        listCommand = new ListCommand(scrapperClient);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
    }

    @Test
    void shouldReturnListOfLinks() {
        ListLinksResponse response = new ListLinksResponse(List.of(
            new LinkResponse(1L, URI.create("http://example.com"))
        ), 1);

        when(scrapperClient.getLinks(123L)).thenReturn(Optional.of(response));

        SendMessage result = listCommand.handle(update);

        String expectedMessage = "your subscriptions:\nhttp://example.com\n";
        assertEquals(expectedMessage, result.getParameters().get("text"));
    }

    @Test
    void shouldReturnMessageWhenNoSubscriptions() {
        ListLinksResponse response = new ListLinksResponse(List.of(), 0);

        when(scrapperClient.getLinks(123L)).thenReturn(Optional.of(response));

        SendMessage result = listCommand.handle(update);

        assertEquals("You aren't subscribed to anything", result.getParameters().get("text"));
    }

    @Test
    void shouldReturnErrorMessageOnFailure() {
        when(scrapperClient.getLinks(123L)).thenThrow(new RuntimeException("Error fetching data"));

        SendMessage result = listCommand.handle(update);

        assertEquals("Error fetching data", result.getParameters().get("text"));
    }

    @Test
    void shouldHandleWrongCommandInput() {
        when(message.text()).thenReturn("/wrong");

        SendMessage result = listCommand.handle(update);

        assertEquals("The command was entered incorrectly! Type /list to view your subscriptions", result.getParameters().get("text"));
    }
}

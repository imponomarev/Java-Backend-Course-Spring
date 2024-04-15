package edu.java.bot.commandTests;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.commands.StartCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private Chat chat;

    @InjectMocks
    private StartCommand startCommand;

    @BeforeEach
    void setUp() {
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
    }

    @Test
    void handleStartTest() {
        when(scrapperClient.registerChat(anyLong())).thenReturn(Optional.of("Success"));

        SendMessage response = startCommand.handle(update);

        assertEquals("You have successfully registered", response.getParameters().get("text"));
        assertEquals(123L, response.getParameters().get("chat_id"));
        verify(scrapperClient).registerChat(123L);
    }

    @Test
    void handleStartCommandWithWrongCommandTest() {
        when(message.text()).thenReturn("/wrong_command");

        SendMessage response = startCommand.handle(update);

        assertEquals("Enter /start to register in the bot", response.getParameters().get("text"));
        assertEquals(123L, response.getParameters().get("chat_id"));
    }
}

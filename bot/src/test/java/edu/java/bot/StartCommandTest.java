package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.dao.MapStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StartCommandTest {

    @Autowired
    Command startCommand;

    @Autowired
    MapStorage mapStorage;

    @AfterEach
    public void clearStorage() {
        mapStorage.clear();
    }

    @Mock
    Update update;

    @Mock
    Message message;

    @Mock
    Chat chat;

    @Test
    void StartCommandTestWithRightInputAndRepeatedInput() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/start");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        SendMessage sendMessage1 = startCommand.handle(update);
        SendMessage sendMessage2 = startCommand.handle(update);

        Assertions.assertEquals(
            "You have successfully registered",
            sendMessage1.getParameters().get("text")
        );
        Assertions.assertEquals(
            11L,
            sendMessage1.getParameters().get("chat_id")
        );

        Assertions.assertEquals(
            "You are already registered",
            sendMessage2.getParameters().get("text")
        );
        Assertions.assertEquals(
            11L,
            sendMessage2.getParameters().get("chat_id")
        );
    }

    @Test
    void StartCommandTestWithWrongInput() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/star");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        SendMessage sendMessage1 = startCommand.handle(update);

        Assertions.assertEquals(
            "Enter /start to register in the bot",
            sendMessage1.getParameters().get("text")
        );
        Assertions.assertEquals(
            11L,
            sendMessage1.getParameters().get("chat_id")
        );
    }
}

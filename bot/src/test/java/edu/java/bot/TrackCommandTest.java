package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.dao.MapStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.URI;

@SpringBootTest
class TrackCommandTest {

    @Autowired
    public Command trackCommand;

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
    void TrackCommandTestWithRightInput() {

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track https://github.com/imponomarev");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        mapStorage.registrate(update);

        SendMessage sendMessage = trackCommand.handle(update);

        Assertions.assertEquals(
            "you have successfully subscribed to the resource",
            sendMessage.getParameters().get("text")
        );
        Assertions.assertEquals(
            11L,
            sendMessage.getParameters().get("chat_id")
        );
    }

    @Test
    void TrackCommandTestWithAlreadyTrackedLink() throws Exception {

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track https://github.com/imponomarev");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        var link = URI.create("https://github.com/imponomarev");

        mapStorage.registrate(update);
        mapStorage.addSubscription(update, link);

        SendMessage sendMessage = trackCommand.handle(update);

        Assertions.assertEquals(
            "You have already subscribed to this resource",
            sendMessage.getParameters().get("text")
        );
        Assertions.assertEquals(
            11L,
            sendMessage.getParameters().get("chat_id")
        );
    }
}

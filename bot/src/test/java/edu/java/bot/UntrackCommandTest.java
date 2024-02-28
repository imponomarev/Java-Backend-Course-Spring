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
class UntrackCommandTest {

    @Autowired
    public Command untrackCommand;

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
    void UntrackCommandTestWithRightInput() throws Exception {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/untrack https://github.com/imponomarev");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        mapStorage.registrate(update);
        var link = URI.create("https://github.com/imponomarev");
        mapStorage.addSubscription(update, link);

        SendMessage sendMessage = untrackCommand.handle(update);

        Assertions.assertEquals(
            "you have successfully unsubscribed from the resource",
            sendMessage.getParameters().get("text")
        );

        Assertions.assertEquals(
            11L,
            sendMessage.getParameters().get("chat_id")
        );
    }

}

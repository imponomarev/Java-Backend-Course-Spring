package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.*;
import edu.java.bot.processor.CommandHolder;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserMessageProcessorTest {

    @Autowired
    private UserMessageProcessor userMessageProcessor;

    @Mock
    private CommandHolder commandHolder;

    @Mock
    private HelpCommand mockHelpCommand;

    @Mock
    private ListCommand mockListCommand;

    @Mock
    private StartCommand mockStartCommand;

    @Mock
    private TrackCommand mockTrackCommand;

    @Mock
    private UntrackCommand mockUntrackCommand;

    @Mock
    Update update;

    @Mock
    Message message;

    @Mock
    Chat chat;

    @BeforeEach
    void setUp() {
        List<Command> commandList = Arrays.asList(
            mockHelpCommand,
            mockListCommand,
            mockStartCommand,
            mockTrackCommand,
            mockUntrackCommand
        );
        when(commandHolder.getCommand("/help")).thenReturn(mockHelpCommand);
        when(commandHolder.getCommand("/list")).thenReturn(mockListCommand);
        when(commandHolder.getCommand("/start")).thenReturn(mockStartCommand);
        when(commandHolder.getCommand("/track")).thenReturn(mockTrackCommand);
        when(commandHolder.getCommand("/untrack")).thenReturn(mockUntrackCommand);

        when(mockHelpCommand.command()).thenReturn("/help");
        when(mockListCommand.command()).thenReturn("/list");
        when(mockStartCommand.command()).thenReturn("/start");
        when(mockTrackCommand.command()).thenReturn("/track");
        when(mockUntrackCommand.command()).thenReturn("/untrack");
    }

    @Test
    void testProcessWithExistingCommand() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        Mockito.when(message.text()).thenReturn("/start");
        SendMessage response1 = userMessageProcessor.process(update);

        Mockito.when(message.text()).thenReturn("/help");
        SendMessage response2 = userMessageProcessor.process(update);

        Mockito.when(message.text()).thenReturn("/list");
        SendMessage response3 = userMessageProcessor.process(update);

        Mockito.when(message.text())
            .thenReturn("/track https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        SendMessage response4 = userMessageProcessor.process(update);

        Mockito.when(message.text())
            .thenReturn("/untrack https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        SendMessage response5 = userMessageProcessor.process(update);

        Assertions.assertEquals(
            "You have successfully registered",
            response1.getParameters().get("text")
        );

        Assertions.assertEquals(
            "Available commands:\n" +
                "/list - show a list of tracked links\n" +
                "/start - register a user\n" +
                "/track - start tracking link\n" +
                "/untrack - stop tracking the link\n",
            response2.getParameters().get("text")
        );

        Assertions.assertEquals(
            "You aren't subscribed to anything",
            response3.getParameters().get("text")
        );

        Assertions.assertEquals(
            "you have successfully subscribed to the resource",
            response4.getParameters().get("text")
        );

        Assertions.assertEquals(
            "you have successfully unsubscribed from the resource",
            response5.getParameters().get("text")
        );
    }

    @Test
    void testProcessWithUnexistingCommand() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        Mockito.when(message.text()).thenReturn("/ssssss");
        SendMessage response = userMessageProcessor.process(update);

        Assertions.assertEquals(
            "Wrong command! Please try again.",
            response.getParameters().get("text")
        );
    }
}

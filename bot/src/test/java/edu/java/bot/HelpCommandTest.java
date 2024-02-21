package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.dao.IStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HelpCommandTest {

    @Mock
    IStorage mapStorage;

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
    void HelpCommandTestWithRightInput() {
        ListCommand listCommand = Mockito.mock(ListCommand.class);
        Mockito.when(listCommand.command()).thenReturn("/list");
        Mockito.when(listCommand.description()).thenReturn("show a list of tracked links");

        StartCommand startCommand = Mockito.mock(StartCommand.class);
        Mockito.when(startCommand.command()).thenReturn("/start");
        Mockito.when(startCommand.description()).thenReturn("register a user");

        TrackCommand trackCommand = Mockito.mock(TrackCommand.class);
        Mockito.when(trackCommand.command()).thenReturn("/track");
        Mockito.when(trackCommand.description()).thenReturn("start tracking link");

        UntrackCommand untrackCommand = Mockito.mock(UntrackCommand.class);
        Mockito.when(untrackCommand.command()).thenReturn("/untrack");
        Mockito.when(untrackCommand.description()).thenReturn("stop tracking the link");

        List<Command> commandList = Arrays.asList(listCommand, startCommand, trackCommand, untrackCommand);

        HelpCommand helpCommand = new HelpCommand(commandList);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/help");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        Mockito.lenient().doNothing().when(mapStorage).registrate(update);

        SendMessage sendMessage = helpCommand.handle(update);
        Assertions.assertEquals(
            "Available commands:\n" +
                "/list - show a list of tracked links\n" +
                "/start - register a user\n" +
                "/track - start tracking link\n" +
                "/untrack - stop tracking the link\n",
            sendMessage.getParameters().get("text")
        );

        Assertions.assertEquals(
            11L,
            sendMessage.getParameters().get("chat_id")
        );
    }

    @Test
    void HelpCommandTestWithIncorrectInput() {

        ListCommand listCommand = Mockito.mock(ListCommand.class);

        StartCommand startCommand = Mockito.mock(StartCommand.class);

        TrackCommand trackCommand = Mockito.mock(TrackCommand.class);

        UntrackCommand untrackCommand = Mockito.mock(UntrackCommand.class);

        List<Command> commandList = Arrays.asList(listCommand, startCommand, trackCommand, untrackCommand);

        HelpCommand helpCommand = new HelpCommand(commandList);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/hell");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);

        Mockito.lenient().doNothing().when(mapStorage).registrate(update);

        SendMessage sendMessage = helpCommand.handle(update);
        Assertions.assertEquals(
            "The command was entered incorrectly!"
                + " Type /help to view the available commands",
            sendMessage.getParameters().get("text")
        );

        Assertions.assertEquals(
            11L,
            sendMessage.getParameters().get("chat_id")
        );
    }
}

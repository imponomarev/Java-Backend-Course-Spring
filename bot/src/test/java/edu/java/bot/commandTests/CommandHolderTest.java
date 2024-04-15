package edu.java.bot.commandTests;

import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.processor.CommandHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CommandHolderTest {

    @Autowired
    private CommandHolder commandHolder;

    @Autowired
    private HelpCommand helpCommand;

    @Autowired
    private ListCommand listCommand;

    @Autowired
    private StartCommand startCommand;

    @Autowired
    private TrackCommand trackCommand;

    @Autowired
    private UntrackCommand untrackCommand;

    @BeforeEach
    void setUp() {
        List<Command> commandList = Arrays.asList(
            helpCommand,
            listCommand,
            startCommand,
            trackCommand,
            untrackCommand
        );
        commandHolder = new CommandHolder(commandList);
    }

    @Test
    void getCommandShouldReturnCommandByName() {

        String name = helpCommand.command();
        Command command = commandHolder.getCommand(name);
        assertNotNull(command);
        assertEquals("/help", command.command());
        assertEquals("provides a list of available commands", command.description());

        String name2 = listCommand.command();
        Command command2 = commandHolder.getCommand(name2);
        assertNotNull(command);
        assertEquals("/list", command2.command());
        assertEquals("show a list of tracked links", command2.description());

        String name3 = startCommand.command();
        Command command3 = commandHolder.getCommand(name3);
        assertNotNull(command);
        assertEquals("/start", command3.command());
        assertEquals("register a user", command3.description());

        String name4 = trackCommand.command();
        Command command4 = commandHolder.getCommand(name4);
        assertNotNull(command);
        assertEquals("/track", command4.command());
        assertEquals("start tracking link", command4.description());

        String name5 = untrackCommand.command();
        Command command5 = commandHolder.getCommand(name5);
        assertNotNull(command);
        assertEquals("/untrack", command5.command());
        assertEquals("stop tracking the link", command5.description());

    }

    @Test
    void getCommandShouldReturnNullIfCommandNotFound() {
        Command command = commandHolder.getCommand("/unknown");
        assertNull(command);
    }

    @Test
    void getCommandsShouldReturnAllCommands() {
        List<Command> commands = commandHolder.getCommands();
        assertEquals(5, commands.size());
        assertTrue(commands.contains(helpCommand));
        assertTrue(commands.contains(listCommand));
        assertTrue(commands.contains(startCommand));
        assertTrue(commands.contains(trackCommand));
        assertTrue(commands.contains(untrackCommand));
    }
}

package edu.java.bot.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import edu.java.bot.commands.Command;

@Component
public class CommandHolder {

    private final Map<String, Command> commandMap;

    public CommandHolder(List<Command> commandList) {
        commandMap = new HashMap<>();
        for (Command command : commandList) {
            commandMap.put(command.command(), command);
        }
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commandMap.values());
    }
}

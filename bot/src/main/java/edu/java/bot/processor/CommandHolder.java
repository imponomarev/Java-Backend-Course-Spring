package edu.java.bot.processor;

import edu.java.bot.commands.Command;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

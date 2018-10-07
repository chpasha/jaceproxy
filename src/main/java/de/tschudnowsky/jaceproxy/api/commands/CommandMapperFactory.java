package de.tschudnowsky.jaceproxy.api.commands;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 18:55
 */
public class CommandMapperFactory {

    private static final Map<Class<? extends Command>, CommandMapper<? extends Command>> map = new HashMap<Class<? extends Command>, CommandMapper<? extends Command>>() {
        {
            put(HelloCommand.class, new HelloCommandMapper());
            put(ReadyCommand.class, new ReadyCommandMapper());
        }
    };

    @SuppressWarnings("unchecked")
    @Nullable
    public static CommandMapper<Command> getCommmandMapper(Command command) {
        return (CommandMapper<Command>) map.get(command.getClass());
    }

}

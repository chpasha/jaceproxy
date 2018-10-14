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
            put(UserDataCommand.class, new UserDataCommandMapper());
            put(SetOptionsCommand.class, new SetOptionsCommandMapper());

            LoadAsyncCommandMapper<LoadAsyncCommand> loadAsyncMapper = new LoadAsyncCommandMapper<>();
            put(LoadAsyncContentIDCommand.class, loadAsyncMapper);
            put(LoadAsyncInfohashCommand.class, loadAsyncMapper);
            put(LoadAsyncRawTransportFileCommand.class, loadAsyncMapper);
            put(LoadAsyncTorrentCommand.class, loadAsyncMapper);
        }
    };

    @SuppressWarnings("unchecked")
    @Nullable
    public static  <T extends Command> CommandMapper<T> getCommandMapper(T command) {
        return (CommandMapper<T>) map.get(command.getClass());
    }

}

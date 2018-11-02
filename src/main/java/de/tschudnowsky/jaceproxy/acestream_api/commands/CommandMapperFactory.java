package de.tschudnowsky.jaceproxy.acestream_api.commands;

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

            StartCommandMapper<StartCommand> startCommandMapper = new StartCommandMapper<>();
            put(StartFileCommand.class, startCommandMapper);
            put(StartInfohashCommand.class, startCommandMapper);
            put(StartPidCommand.class, startCommandMapper);
            put(StartRawCommand.class, startCommandMapper);
            put(StartTorrentCommand.class, startCommandMapper);
            put(StartUrlCommand.class, startCommandMapper);

            put(SeekCommand.class, new SeekCommandMapper());

            SimpleCommandMapper simpleCommandMapper = new SimpleCommandMapper();
            put(StopCommand.class, simpleCommandMapper);
            put(ShutdownCommand.class, simpleCommandMapper);
        }
    };

    @SuppressWarnings("unchecked")
    @Nullable
    public static  <T extends Command> CommandMapper<T> getCommandMapper(T command) {
        return (CommandMapper<T>) map.get(command.getClass());
    }

}

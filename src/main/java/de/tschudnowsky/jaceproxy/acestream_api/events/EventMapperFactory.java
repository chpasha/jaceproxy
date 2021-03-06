package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static de.tschudnowsky.jaceproxy.acestream_api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 16:55
 */
public class EventMapperFactory {

    private static final Map<String, EventMapper<? extends Event>> map = new HashMap<String, EventMapper<? extends Event>>() {
        {
            put("AUTH", new AuthEventMapper());
            put("EVENT", new GenericEventMapper());
            put("HELLOTS", new HelloEventMapper());
            put("INFO", new InfoEventMapper());
            put("NOTREADY", new EmptyEventMapper<>(NotReadyEvent.class));
            put("RESUME", new EmptyEventMapper<>(ResumeEvent.class));
            put("PAUSE", new EmptyEventMapper<>(PauseEvent.class));
            put("STATUS", new StatusEventMapper());
            put("LOADRESP", new LoadAsyncResponseEventMapper());
            put("STATE", new StateEventMapper());
            put("START", new StartPlayEventMapper());
            put("STOP", new EmptyEventMapper<>(StopEvent.class));
            put("SHUTDOWN", new EmptyEventMapper<>(ShutdownEvent.class));
        }
    };

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends Event> EventMapper<T> findMapper(String rawEvent) {
        int startOfProperties = rawEvent.indexOf(PROPERTY_SEPARATOR);
        boolean hasProperties = startOfProperties > -1;
        return (EventMapper<T>) map.get(hasProperties
                ? rawEvent.substring(0, startOfProperties)
                : rawEvent);
    }
}

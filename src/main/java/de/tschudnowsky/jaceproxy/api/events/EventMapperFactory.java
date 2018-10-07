package de.tschudnowsky.jaceproxy.api.events;

import org.jetbrains.annotations.Nullable;

import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 16:55
 */
public enum EventMapperFactory {

    HELLO("HELLOTS", new HelloEventMapper()),
    NOT_READY("NOTREADY", new NotReadyEventMapper()),
    AUTH("AUTH", new AuthEventMapper())
    ;

    private String event;
    private EventMapper<?> eventMapper;

    EventMapperFactory(String event, EventMapper<?> eventMapper) {
        this.event = event;
        this.eventMapper = eventMapper;
    }

    @Nullable
    public static EventMapper<?> findMapper(String rawEvent) {
        for (EventMapperFactory factory : values()) {
            if (defaultString(rawEvent).startsWith(factory.event)) {
                return factory.eventMapper;
            }
        }
        return null;
    }
}

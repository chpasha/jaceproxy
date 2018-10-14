package de.tschudnowsky.jaceproxy.api.events;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static de.tschudnowsky.jaceproxy.api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
@Slf4j
public class GenericEventMapper implements EventMapper<Event> {

    private static final Map<String, Class<? extends Event>> eventMap = new HashMap<String, Class<? extends Event>>() {
        {
            put("getuserdata", GetUserDataEvent.class);
            put("showurl", NotSupportedEvent.class);
        }
    };

    private static final Map<String, EventMapper<? extends Event>> eventMapperMap = new HashMap<String, EventMapper<? extends Event>>() {
        {
            put("cansave", new CanSaveEventMapper());
            put("livepos", new LiveposEventMapper());
            put("download_stopped", new DownloadStoppedEventMapper());
        }
    };


    @Override
    public Event readValue(String rawValue) {

        //Events can be without properties or with them
        //EVENT getuserdata
        //EVENT cansave infohash=infohash index=index format=format

        int startOfFirstProperty = rawValue.indexOf(PROPERTY_SEPARATOR);
        boolean hasProperties = startOfFirstProperty > -1;
        String eventType = hasProperties ? rawValue.substring(0, startOfFirstProperty) : rawValue;
        String eventProperties = hasProperties ? rawValue.substring(startOfFirstProperty).trim() : null;

        if (hasProperties) {
            return createEventWithProperties(eventType, eventProperties);
        } else {
            return createSimpleEvent(eventType);
        }
    }

    private Event createEventWithProperties(String eventType, String eventProperties) {
        EventMapper<? extends Event> eventMapper = eventMapperMap.get(eventType);
        if (eventMapper != null) {
            return eventMapper.readValue(eventProperties);
        } else {
            log.error("Unknown generic event {}", eventType);
            return new NotSupportedEvent();
        }
    }

    @Nullable
    private Event createSimpleEvent(String eventType) {
        try {
            Class<? extends Event> aClass = eventMap.get(eventType);
            if (aClass != null) {
                return aClass.newInstance();
            } else {
                log.error("Unknown generic event {}", eventType);
            }
        } catch (Exception e) {
            log.error("Instantiate generic event", e);
        }
        return new NotSupportedEvent();
    }
}

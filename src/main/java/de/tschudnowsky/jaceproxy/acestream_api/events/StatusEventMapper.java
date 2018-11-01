package de.tschudnowsky.jaceproxy.acestream_api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class StatusEventMapper extends EventMapperImpl<StatusEvent> {

    private final Map<String, BiConsumer<StatusEvent, String>> map = new HashMap<String, BiConsumer<StatusEvent, String>>() {
        {
            put("", StatusEvent::setStatus);
        }
    };


    StatusEventMapper() {
        super(StatusEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<StatusEvent, String>> getPropertyMappings() {
        return map;
    }
}

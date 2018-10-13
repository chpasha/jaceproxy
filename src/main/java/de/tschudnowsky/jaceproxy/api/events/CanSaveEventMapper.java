package de.tschudnowsky.jaceproxy.api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class CanSaveEventMapper extends EventMapperImpl<CanSaveEvent> {

    private final Map<String, BiConsumer<CanSaveEvent, String>> map = new HashMap<String, BiConsumer<CanSaveEvent, String>>() {
        {
            put("format", CanSaveEvent::setFormat);
            put("infohash", CanSaveEvent::setInfohash);
            put("index", (e, v) -> e.setIndex(toInt(v)));
        }
    };


    CanSaveEventMapper() {
        super(CanSaveEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<CanSaveEvent, String>> getPropertyMappings() {
        return map;
    }
}

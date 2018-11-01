package de.tschudnowsky.jaceproxy.acestream_api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class StartPlayEventMapper extends EventMapperImpl<StartPlayEvent> {

    private final Map<String, BiConsumer<StartPlayEvent, String>> map = new HashMap<String, BiConsumer<StartPlayEvent, String>>() {
        {
            put("", StartPlayEvent::setUrl);
            put("stream", (e,v) -> e.setLiveStream("1".equals(v)));
        }
    };

    StartPlayEventMapper() {
        super(StartPlayEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<StartPlayEvent, String>> getPropertyMappings() {
        return map;
    }
}

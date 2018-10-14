package de.tschudnowsky.jaceproxy.api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class StartEventMapper extends EventMapperImpl<StartEvent> {

    private final Map<String, BiConsumer<StartEvent, String>> map = new HashMap<String, BiConsumer<StartEvent, String>>() {
        {
            put("", StartEvent::setUrl);
            put("stream", (e,v) -> e.setLiveStream("1".equals(v)));
        }
    };

    StartEventMapper() {
        super(StartEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<StartEvent, String>> getPropertyMappings() {
        return map;
    }
}

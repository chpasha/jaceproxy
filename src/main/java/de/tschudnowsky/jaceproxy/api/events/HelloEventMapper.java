package de.tschudnowsky.jaceproxy.api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class HelloEventMapper extends EventMapperImpl<HelloEvent>  {

    private final Map<String, BiConsumer<HelloEvent, String>> map = new HashMap<String, BiConsumer<HelloEvent, String>>() {
        {
            put("bmode", HelloEvent::setBmode);
            put("version", HelloEvent::setEngineVersion);
            put("version_code", HelloEvent::setVersionCode);
            put("key", HelloEvent::setRequestKey);
            put("http_port", (h, v) -> h.setHttpPort(toInt(v)));
        }
    };


    HelloEventMapper() {
        super(HelloEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<HelloEvent, String>> getPropertyMappings() {
        return map;
    }
}

package de.tschudnowsky.jaceproxy.acestream_api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class DownloadStoppedEventMapper extends EventMapperImpl<DownloadStoppedEvent> {

    private final Map<String, BiConsumer<DownloadStoppedEvent, String>> map = new HashMap<String, BiConsumer<DownloadStoppedEvent, String>>() {
        {
            put("reason", DownloadStoppedEvent::setReason);
            put("option", DownloadStoppedEvent::setOption);
        }
    };


    DownloadStoppedEventMapper() {
        super(DownloadStoppedEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<DownloadStoppedEvent, String>> getPropertyMappings() {
        return map;
    }
}

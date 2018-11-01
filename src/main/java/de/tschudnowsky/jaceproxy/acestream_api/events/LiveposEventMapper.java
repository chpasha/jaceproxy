package de.tschudnowsky.jaceproxy.acestream_api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class LiveposEventMapper extends EventMapperImpl<LiveposEvent> {

    private final Map<String, BiConsumer<LiveposEvent, String>> map = new HashMap<String, BiConsumer<LiveposEvent, String>>() {
        {
            put("last", LiveposEvent::setLast);
            put("live_first", LiveposEvent::setLiveFirst);
            put("pos", LiveposEvent::setPos);
            put("first_ts", LiveposEvent::setFirstTs);
            put("last_ts", LiveposEvent::setLastTs);
            put("is_live", (e, v) -> e.setLive("1".equals(v)));
            put("buffer_pieces", (e, v) -> e.setBufferPieces(toInt(v)));
            put("live_last", LiveposEvent::setLiveLast);
        }
    };


    LiveposEventMapper() {
        super(LiveposEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<LiveposEvent, String>> getPropertyMappings() {
        return map;
    }
}

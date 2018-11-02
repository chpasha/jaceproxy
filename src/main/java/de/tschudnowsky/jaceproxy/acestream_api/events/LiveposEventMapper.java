package de.tschudnowsky.jaceproxy.acestream_api.events;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class LiveposEventMapper extends EventMapperImpl<LiveposEvent> {

    private Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

    private final Map<String, BiConsumer<LiveposEvent, String>> map = new HashMap<String, BiConsumer<LiveposEvent, String>>() {
        {
            put("last", (e, val) -> e.setLast(toTime(val)));
            put("live_first", (e, val) -> e.setLiveFirst(toTime(val)));
            put("pos", (e, val) -> e.setPos(toTime(val)));
            put("first_ts", (e, val) -> e.setFirstTs(toTime(val)));
            put("last_ts", (e, val) -> e.setLastTs(toTime(val)));
            put("is_live", (e, v) -> e.setLive("1".equals(v)));
            put("buffer_pieces", (e, v) -> e.setBufferPieces(toInt(v)));
            put("live_last", (e, val) -> e.setLiveLast(toTime(val)));
        }
    };


    LiveposEventMapper() {
        super(LiveposEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<LiveposEvent, String>> getPropertyMappings() {
        return map;
    }

    private String toTime(String value) {
        Long val = toLong(value);
        if (val != null) {
            calendar.setTimeInMillis(val * 1000);
            return String.format("%s:%s:%s", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        } else {
            return value;
        }
    }
}

package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class LiveposEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "EVENT livepos last=1539535944 live_first=1539534144 pos=1539535944 first_ts=1539534144 last_ts=1539535944 is_live=1 live_last=1539535944 buffer_pieces=15";

        LiveposEvent event = createEvent(rawEvent);

        assertThat(event).isNotNull();
    }
}

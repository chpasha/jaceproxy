package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class CanSaveEventTest extends EventMapperTest {

    @Test
    public void testReadValue() {
        String infohash = UUID.randomUUID().toString();
        Integer index = 0;
        String format = "plain";
        String rawEvent = String.format("EVENT cansave infohash=%s index=%s format=%s",
                infohash, index, format);

        CanSaveEvent event = createEvent(rawEvent);
        assertThat(event).isNotNull();
        assertThat(event.getFormat()).isEqualTo(format);
        assertThat(event.getIndex()).isEqualTo(index);
        assertThat(event.getInfohash()).isEqualTo(infohash);
    }

}

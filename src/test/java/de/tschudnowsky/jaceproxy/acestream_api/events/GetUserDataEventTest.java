package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class GetUserDataEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "EVENT getuserdata";

        GetUserDataEvent event = createEvent(rawEvent);
        assertThat(event).isNotNull();
    }
}

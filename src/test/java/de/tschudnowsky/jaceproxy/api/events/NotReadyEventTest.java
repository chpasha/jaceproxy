package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class NotReadyEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "NOTREADY";

        NotReadyEvent event = createEvent(rawEvent);
        assertThat(event).isNotNull();
    }
}

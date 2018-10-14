package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class StatusEventTest extends EventMapperTest {

    @Test
    public void testReadValue() {
        String rawEvent = "STATUS main:loading";

        StatusEvent event = createEvent(rawEvent);

        assertThat(event).isNotNull();
        assertThat(event.getStatus()).isEqualTo("main:loading");
    }
}

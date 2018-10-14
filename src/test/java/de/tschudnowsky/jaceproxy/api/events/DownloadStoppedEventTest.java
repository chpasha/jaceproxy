package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class DownloadStoppedEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "EVENT download_stopped reason=missing_option option=proxyServer";

        DownloadStoppedEvent event = createEvent(rawEvent);

        assertThat(event).isNotNull();
    }
}

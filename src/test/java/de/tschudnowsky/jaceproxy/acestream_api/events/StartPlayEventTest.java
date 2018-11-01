package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class StartPlayEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "START http://127.0.0.1:6878/content/553b7d4cfec8974752d386844cb67e0ee64eae05/0.728180367195 stream=1";

        StartPlayEvent auth = createEvent(rawEvent);

        assertThat(auth).isNotNull();
        assertThat(auth.getUrl()).isNotBlank();
        assertThat(auth.isLiveStream()).isTrue();
    }
}

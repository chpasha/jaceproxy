package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class InfoEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "INFO 1;Cannot find active peers";

        InfoEvent info = createEvent(rawEvent);

        assertThat(info).isNotNull();
        assertThat(info.getCode()).isEqualTo(1);
        assertThat(info.getDescription()).isEqualTo("Cannot find active peers");
    }
}

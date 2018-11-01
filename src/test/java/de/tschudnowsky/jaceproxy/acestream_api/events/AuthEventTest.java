package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class AuthEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "AUTH 1";

        AuthEvent auth = createEvent(rawEvent);

        assertThat(auth).isNotNull();
        assertThat(auth.isRegisteredUser()).isTrue();
    }
}

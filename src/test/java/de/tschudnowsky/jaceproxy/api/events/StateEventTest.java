package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class StateEventTest extends EventMapperTest{

    @Test
    public void testReadValue() {
        String rawEvent = "STATE 5";

        StateEvent auth = createEvent(rawEvent);

        assertThat(auth).isNotNull();
        assertThat(auth.getState()).isEqualTo(5);
        assertThat(auth.getDescription()).isNotBlank();
    }
}

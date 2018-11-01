package de.tschudnowsky.jaceproxy.acestream_api.events;

import static de.tschudnowsky.jaceproxy.acestream_api.Message.PROPERTY_SEPARATOR;
import static org.junit.Assert.assertNotNull;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 20:33
 */
public class EventMapperTest {
    protected <T extends Event> T createEvent(String rawEvent) {
        EventMapper<T> mapper = createMapper(rawEvent);
        assertNotNull(mapper);

        int startOfProperties = rawEvent.indexOf(PROPERTY_SEPARATOR);
        if (startOfProperties > -1) {
            rawEvent = rawEvent.substring(startOfProperties).trim();
        }
        return mapper.readValue(rawEvent);
    }

    private  <T extends Event> EventMapper<T> createMapper(String rawEvent) {
        EventMapper<T> mapper = EventMapperFactory.findMapper(rawEvent);
        assertNotNull(mapper);
        return mapper;
    }
}

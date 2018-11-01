package de.tschudnowsky.jaceproxy.acestream_api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class AuthEventMapper extends EventMapperImpl<AuthEvent> {

    private final Map<String, BiConsumer<AuthEvent, String>> map = new HashMap<String, BiConsumer<AuthEvent, String>>() {
        {
            put("", (e, v) -> e.setRegisteredUser("1".equals(v)));
        }
    };


    AuthEventMapper() {
        super(AuthEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<AuthEvent, String>> getPropertyMappings() {
        return map;
    }
}

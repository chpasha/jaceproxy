package de.tschudnowsky.jaceproxy.api.events;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class EmptyEventMapper<T extends Event> extends EventMapperImpl<T> {

    EmptyEventMapper(Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected Map<String, BiConsumer<T, String>> getPropertyMappings() {
        return Collections.emptyMap();
    }
}

package de.tschudnowsky.jaceproxy.api.events;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class NotReadyEventMapper extends EventMapperImpl<NotReadyEvent>  {


    NotReadyEventMapper() {
        super(NotReadyEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<NotReadyEvent, String>> getPropertyMappings() {
        return Collections.emptyMap();
    }
}

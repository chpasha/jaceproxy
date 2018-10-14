package de.tschudnowsky.jaceproxy.api.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class StateEventMapper extends EventMapperImpl<StateEvent> {

    private static final Map<String, String> stateMap = new HashMap<String, String>() {
        {
            put("0", "Idle");
            put("1", "Pre-buffering");
            put("2", "Downloading");
            put("3", "Buffering");
            put("4", "Load Content Completed");
            put("5", "Checking");
            put("6", "Error");
        }
    };

    private final Map<String, BiConsumer<StateEvent, String>> map = new HashMap<String, BiConsumer<StateEvent, String>>() {
        {
            put("", (e, v) -> {
                e.setState(toInt(v));
                e.setDescription(stateMap.get(v));
            });
        }
    };


    StateEventMapper() {
        super(StateEvent.class);
    }

    @Override
    protected Map<String, BiConsumer<StateEvent, String>> getPropertyMappings() {
        return map;
    }
}

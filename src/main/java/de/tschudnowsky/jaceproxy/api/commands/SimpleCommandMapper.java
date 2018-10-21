package de.tschudnowsky.jaceproxy.api.commands;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * User: pavel
 * Date: 21.10.18
 * Time: 20:18
 */
public class SimpleCommandMapper extends CommandMapperImpl<Command> {

    @Override
    protected Map<String, Function<Command, String>> getPropertyMappings() {
        return Collections.emptyMap();
    }
}

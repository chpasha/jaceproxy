package de.tschudnowsky.jaceproxy.api.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:12
 */
public class ReadyCommandMapper extends CommandMapperImpl<ReadyCommand> {

    private final Map<String, Function<ReadyCommand, String>> map = new HashMap<String, Function<ReadyCommand, String>>() {
        {
            put("key", ReadyCommand::getKey);
        }
    };

    @Override
    protected Map<String, Function<ReadyCommand, String>> getPropertyMappings() {
        return map;
    }
}

package de.tschudnowsky.jaceproxy.api.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:12
 */
public class HelloCommandMapper extends CommandMapperImpl<HelloCommand> {

    private final Map<String, Function<HelloCommand, String>> map = new HashMap<String, Function<HelloCommand, String>>() {
        {
            put("version", hello -> HelloCommandMapper.this.toString(hello.getVersion()));
        }
    };

    @Override
    protected Map<String, Function<HelloCommand, String>> getPropertyMappings() {
        return map;
    }
}

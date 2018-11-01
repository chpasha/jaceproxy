package de.tschudnowsky.jaceproxy.acestream_api.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:12
 */
public class SetOptionsCommandMapper extends CommandMapperImpl<SetOptionsCommand> {

    private final Map<String, Function<SetOptionsCommand, String>> map = new HashMap<String, Function<SetOptionsCommand, String>>() {
        {
            put("use_stop_notifications", c -> SetOptionsCommandMapper.this.toString(c.isNotifyAboutDownloadStopped()));
        }
    };

    @Override
    protected Map<String, Function<SetOptionsCommand, String>> getPropertyMappings() {
        return map;
    }
}

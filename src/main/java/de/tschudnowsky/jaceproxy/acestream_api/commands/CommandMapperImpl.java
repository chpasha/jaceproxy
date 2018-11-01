package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

import static de.tschudnowsky.jaceproxy.acestream_api.Message.PROPERTY_SEPARATOR;
import static de.tschudnowsky.jaceproxy.acestream_api.Message.VALUE_SEPARATOR;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 22:30
 */
@Slf4j
public abstract class CommandMapperImpl<T extends Command> implements CommandMapper<T> {

    protected abstract Map<String, Function<T, String>> getPropertyMappings();


    @Nullable String toString(Integer value) {
        return value != null ? value.toString() : null;
    }

    @Nullable String toString(Boolean value) {
        return defaultIfNull(value, false) ? "1" : "0";
    }

    @Override
    public CharSequence writeAsString(T command) {
        StringBuilder sb = new StringBuilder(command.getName());

        for (Map.Entry<String, Function<T, String>> mapping : getPropertyMappings().entrySet()) {
            String value = mapping.getValue().apply(command);
            if (isNotBlank(value)) {
                sb.append(PROPERTY_SEPARATOR)
                        .append(mapping.getKey())
                        .append(VALUE_SEPARATOR)
                        .append(value);
            } else {
                log.warn("Command {}: property {} is null", command.getName(), mapping.getKey());
            }
        }
        return sb.toString();
    }
}

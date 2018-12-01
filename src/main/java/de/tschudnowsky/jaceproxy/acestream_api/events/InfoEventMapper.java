package de.tschudnowsky.jaceproxy.acestream_api.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
public class InfoEventMapper extends EventMapperImpl<InfoEvent> {



    InfoEventMapper() {
        super(InfoEvent.class);
    }

    @Nullable
    @Override
    public InfoEvent readValue(@NotNull String value) {
        InfoEvent event = new InfoEvent();
        if (isNotBlank(value)) {
            String[] split = value.split(";");
            if (split.length > 0) {
                event.setCode(toInt(split[0]));
            }
            if (split.length > 1) {
                event.setDescription(split[1]);
            }
        }
        return event;
    }

    @Override
    protected Map<String, BiConsumer<InfoEvent, String>> getPropertyMappings() {
        return Collections.emptyMap();
    }
}

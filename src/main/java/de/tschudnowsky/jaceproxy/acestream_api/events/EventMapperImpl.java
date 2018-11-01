package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

import static de.tschudnowsky.jaceproxy.acestream_api.Message.PROPERTY_SEPARATOR;
import static de.tschudnowsky.jaceproxy.acestream_api.Message.VALUE_SEPARATOR;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 22:30
 */
@Slf4j
@Data
public abstract class EventMapperImpl<T extends Event> implements EventMapper<T> {

    private final Class<T> tClass;

    protected abstract Map<String, BiConsumer<T, String>> getPropertyMappings();


    @Override
    @Nullable
    public T readValue(@NotNull @NonNull String raw) {
        try {
            T t = tClass.newInstance();
            Map<String, BiConsumer<T, String>> map = getPropertyMappings();
            String[] propertyValuePairs = raw.split(PROPERTY_SEPARATOR);
            for (String propertyValuePair : propertyValuePairs) {

                String[] propertyValue = propertyValuePair.split(VALUE_SEPARATOR);
                boolean isValueWithoutPropertyName = propertyValue.length == 1;
                boolean isPropertyValue = propertyValue.length == 2;

                if (isPropertyValue || isValueWithoutPropertyName) {
                    String setterKey = isPropertyValue ? propertyValue[0] : "" /*special case*/;
                    String setterValue = isPropertyValue ? propertyValue[1] : propertyValue[0];
                    BiConsumer<T, String> setter = map.get(setterKey);
                    if (setter != null) {
                        setter.accept(t, setterValue);
                    } else {
                        if (isValueWithoutPropertyName) {
                            log.warn("Single property {} found for class {} without default properties", setterValue, tClass.getSimpleName());
                        } else {
                            log.warn("Unknown property {} for class {}", setterKey, tClass.getSimpleName());
                        }
                    }
                }
            }
            return t;
        } catch (Exception e) {
            log.error("Instantiation of " + tClass, e);
        }
        return null;
    }

    @Nullable Integer toInt(@Nullable String value) {
        try {
            return value == null ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Converting string to int", e);
        }
        return null;
    }
}

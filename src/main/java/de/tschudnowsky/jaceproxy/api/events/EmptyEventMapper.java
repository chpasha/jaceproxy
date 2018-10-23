package de.tschudnowsky.jaceproxy.api.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
@RequiredArgsConstructor
@Slf4j
public class EmptyEventMapper<T extends Event> implements EventMapper<T> {

    private final Class<T> clazz;

    @Override
    public T readValue(String rawValue) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            log.error("Instantiation of " + clazz, e);
        }
        return null;
    }
}

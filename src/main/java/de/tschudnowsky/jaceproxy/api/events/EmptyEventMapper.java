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
    private T instance;

    @Override
    public T readValue(String rawValue) {
        try {
            if (instance == null) {
                instance = clazz.newInstance();
            }
            return instance;
        } catch (Exception e) {
            log.error("Instantiation of " + clazz, e);
        }
        return null;
    }
}

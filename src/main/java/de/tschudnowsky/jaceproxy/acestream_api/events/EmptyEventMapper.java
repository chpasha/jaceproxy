package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.extern.slf4j.Slf4j;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:36
 */
@Slf4j
public class EmptyEventMapper<T extends Event> implements EventMapper<T> {

    private T instance;

    EmptyEventMapper(Class<T> clazz) {
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            log.error("Instantiation of " + clazz, e);
        }
    }

    @Override
    public T readValue(String rawValue) {
        return instance;
    }
}

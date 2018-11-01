package de.tschudnowsky.jaceproxy.acestream_api.events;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 12:55
 */
public interface EventMapper<T extends Event> {

    T readValue(String rawValue);
}

package de.tschudnowsky.jaceproxy.acestream_api.commands;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 12:55
 */
public interface CommandMapper<T extends Command> {

    CharSequence writeAsString(T command);
}

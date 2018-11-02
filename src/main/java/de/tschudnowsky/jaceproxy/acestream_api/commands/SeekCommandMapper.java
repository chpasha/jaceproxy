package de.tschudnowsky.jaceproxy.acestream_api.commands;

import static de.tschudnowsky.jaceproxy.acestream_api.Message.PROPERTY_SEPARATOR;
import static de.tschudnowsky.jaceproxy.acestream_api.Message.VALUE_SEPARATOR;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:12
 */
public class SeekCommandMapper implements CommandMapper<SeekCommand> {

    @Override
    public CharSequence writeAsString(SeekCommand command) {
        return new StringBuilder(command.getName())
                .append(PROPERTY_SEPARATOR)
                .append("seek")
                .append(PROPERTY_SEPARATOR)
                .append("position")
                .append(VALUE_SEPARATOR)
                .append(command.getPosition());
    }
}

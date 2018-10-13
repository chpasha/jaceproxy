package de.tschudnowsky.jaceproxy.api.commands;

import static de.tschudnowsky.jaceproxy.api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:12
 */
public class UserDataCommandMapper implements CommandMapper<UserDataCommand> {

    @Override
    public CharSequence writeAsString(UserDataCommand command) {
        return new StringBuilder(command.getName())
                .append(PROPERTY_SEPARATOR)
                .append(command.getData())
                .toString();
    }
}

package de.tschudnowsky.jaceproxy.api.commands;

import de.tschudnowsky.jaceproxy.api.MessageImpl;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:45
 */
abstract class CommandImpl extends MessageImpl implements Command {

    CommandImpl(String name) {
        super(name);
    }
}

package de.tschudnowsky.jaceproxy.acestream_api.commands;

import de.tschudnowsky.jaceproxy.acestream_api.MessageImpl;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:45
 */
abstract class CommandImpl extends MessageImpl implements Command {

    public CommandImpl() {
    }

    CommandImpl(String name) {
        super(name);
    }
}

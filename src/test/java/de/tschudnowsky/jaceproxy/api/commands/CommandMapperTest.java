package de.tschudnowsky.jaceproxy.api.commands;

import static org.junit.Assert.assertNotNull;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 20:45
 */
public class CommandMapperTest {

    protected <T extends Command> CharSequence writeAsString(T command) {
        return getCommandMapper(command).writeAsString(command);
    }

    private  <T extends Command> CommandMapper<T> getCommandMapper(T command) {
        CommandMapper<T> mapper = CommandMapperFactory.getCommandMapper(command);
        assertNotNull(mapper);
        return mapper;
    }
}

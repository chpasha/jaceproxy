package de.tschudnowsky.jaceproxy.api.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class SimpleCommandMapperTest extends CommandMapperTest{

    @Test
    public void testWriteAsString() {

        Command command = new StopCommand();
        CharSequence result = writeAsString(command);
        assertEquals(result, command.getName());

        command = new ShutdownCommand();
        result = writeAsString(command);
        assertEquals(result, command.getName());
    }

}

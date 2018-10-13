package de.tschudnowsky.jaceproxy.api.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class ReadyCommandMapperTest extends CommandMapperTest{

    @Test
    public void testWriteAsString() {

        ReadyCommand command = new ReadyCommand("123");
        CharSequence result = writeAsString(command);
        assertEquals(result, "READY key=123");
    }

}

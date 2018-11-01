package de.tschudnowsky.jaceproxy.acestream_api.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class HelloCommandMapperTest extends CommandMapperTest {

    @Test
    public void testWriteAsString() {

        HelloCommand helloCommand = new HelloCommand(3);

        CharSequence result = writeAsString(helloCommand);
        assertEquals(result, "HELLOBG version=3");
    }

}

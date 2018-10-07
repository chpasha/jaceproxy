package de.tschudnowsky.jaceproxy.api.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class HelloCommandMapperTest {

    @Test
    public void testWriteAsString() {

        HelloCommandMapper mapper = new HelloCommandMapper();
        HelloCommand helloCommand = new HelloCommand(3);
        CharSequence result = mapper.writeAsString(helloCommand);
        assertEquals(result, "HELLOBG version=3");
    }

}

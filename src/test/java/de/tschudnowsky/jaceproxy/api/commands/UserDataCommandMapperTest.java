package de.tschudnowsky.jaceproxy.api.commands;

import org.junit.Test;

import static de.tschudnowsky.jaceproxy.api.commands.UserDataCommand.FAKE_DATA;
import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class UserDataCommandMapperTest extends CommandMapperTest{

    @Test
    public void testWriteAsString() {

        UserDataCommand command = new UserDataCommand();
        CharSequence result = writeAsString(command);
        assertEquals(result, "USERDATA " + FAKE_DATA);
    }

}

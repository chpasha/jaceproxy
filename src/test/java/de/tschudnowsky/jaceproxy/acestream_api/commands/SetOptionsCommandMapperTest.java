package de.tschudnowsky.jaceproxy.acestream_api.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class SetOptionsCommandMapperTest extends CommandMapperTest{

    @Test
    public void testWriteAsString() {

        SetOptionsCommand command = SetOptionsCommand.builder()
                                                     .notifyAboutDownloadStopped(true)
                                                     .build();

        CharSequence result = writeAsString(command);
        assertEquals(result, "SETOPTIONS use_stop_notifications=1");
    }

}

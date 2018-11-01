package de.tschudnowsky.jaceproxy.acestream_api.commands;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class StartCommandMapperTest extends CommandMapperTest {

    @Test
    public void testWriteAsString() throws UnsupportedEncodingException {

        String value = UUID.randomUUID().toString();
        StartCommand command = new StartFileCommand(value);

        CharSequence result = writeAsString(command);
        assertEquals(result, String.format("START EFILE %s output_format=http", URLEncoder.encode(value, "UTF-8")));

        command = new StartInfohashCommand(value, singletonList(0));
        result = writeAsString(command);
        assertEquals(result, String.format("START INFOHASH %s 0 0 0 0 output_format=http", value));

        command = new StartPidCommand(value, singletonList(0));
        result = writeAsString(command);
        assertEquals(result, String.format("START PID %s 0 output_format=http", value));

        command = new StartRawCommand(value, singletonList(0));
        result = writeAsString(command);
        assertEquals(result, String.format("START RAW %s 0 0 0 0 output_format=http", value));

        command = new StartTorrentCommand(value, singletonList(0), 1);
        result = writeAsString(command);
        assertEquals(result, String.format("START TORRENT %s 0 0 0 0 1 output_format=http", value));

        command = new StartUrlCommand(value, singletonList(0));
        result = writeAsString(command);
        assertEquals(result, String.format("START URL %s 0 0 0 0 output_format=http", value));
    }

}

package de.tschudnowsky.jaceproxy.acestream_api.commands;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:16
 */
public class LoadAsyncCommandMapperTest extends CommandMapperTest {

    @Test
    public void testWriteAsString() {

        String contentId = UUID.randomUUID().toString();
        LoadAsyncCommand command = new LoadAsyncContentIDCommand(contentId);

        CharSequence result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s PID %s", command.getRequestId(), contentId));

        String infohash = UUID.randomUUID().toString();
        command = new LoadAsyncInfohashCommand(infohash);
        result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s INFOHASH %s 0 0 0", command.getRequestId(), infohash));

        String rawContent = UUID.randomUUID().toString();
        command = new LoadAsyncRawTransportFileCommand(rawContent);
        result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s RAW %s 0 0 0", command.getRequestId(), rawContent));

        String torrentUrl = "http://google.com";
        command = new LoadAsyncTorrentCommand(torrentUrl);
        result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s TORRENT %s 0 0 0", command.getRequestId(), torrentUrl));
    }

}

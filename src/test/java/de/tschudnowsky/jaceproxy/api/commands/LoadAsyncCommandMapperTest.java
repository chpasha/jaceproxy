package de.tschudnowsky.jaceproxy.api.commands;

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
        LoadAsyncCommand command = LoadAsyncContentIDCommand.builder()
                                                            .contentId(contentId)
                                                            .build();
        CharSequence result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s PID %s", command.getRequestId(), contentId));

        String infohash = UUID.randomUUID().toString();
        command = LoadAsyncInfohashCommand.builder()
                                          .infohash(infohash)
                                          .build();
        result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s INFOHASH %s 0 0 0", command.getRequestId(), infohash));

        String rawContent = UUID.randomUUID().toString();
        command = LoadAsyncRawTransportFileCommand.builder()
                                          .transportFileAsBase64(rawContent)
                                          .build();
        result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s RAW %s 0 0 0", command.getRequestId(), rawContent));

        String torrentUrl = "http://google.com";
        command = LoadAsyncTorrentCommand.builder()
                                          .torrentUrl(torrentUrl)
                                          .build();
        result = writeAsString(command);
        assertEquals(result, String.format("LOADASYNC %s TORRENT %s 0 0 0", command.getRequestId(), torrentUrl));
    }

}

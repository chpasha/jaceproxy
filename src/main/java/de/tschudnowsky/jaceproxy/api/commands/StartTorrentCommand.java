package de.tschudnowsky.jaceproxy.api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 16:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartTorrentCommand extends StartCommand {

    private final String url;
    private final Iterable<Integer> fileIndexes;
    private final int streamId;

    public StartTorrentCommand(String url, Iterable<Integer> fileIndexes, int streamId) {
        super(Type.TORRENT);
        this.url = url;
        this.fileIndexes = fileIndexes;
        this.streamId = streamId;
    }
}

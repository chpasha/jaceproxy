package de.tschudnowsky.jaceproxy.api.commands;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class LoadAsyncTorrentCommand extends LoadAsyncCommand {

    private String torrentUrl;

    private LoadAsyncTorrentCommand(String torrentUrl) {
        this();
        this.torrentUrl = torrentUrl;
    }

    public LoadAsyncTorrentCommand() {
        super(Type.TORRENT);
    }
}

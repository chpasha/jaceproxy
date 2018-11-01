package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoadAsyncTorrentCommand extends LoadAsyncCommand {

    private String torrentUrl;

    public LoadAsyncTorrentCommand(String torrentUrl) {
        super(Type.TORRENT);
        this.torrentUrl = torrentUrl;
    }
}

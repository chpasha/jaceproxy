package de.tschudnowsky.jaceproxy.api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 16:21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class StartCommand extends CommandImpl {
    private final String developerAffiliateZone = "0 0 0";

    private final Type type;
    private int fileIndex;

    enum Type {
        TORRENT, INFOHASH, RAW, PID, URL, EFILE
    }

    StartCommand(Type type) {
        super("START");
        this.type = type;
    }
}

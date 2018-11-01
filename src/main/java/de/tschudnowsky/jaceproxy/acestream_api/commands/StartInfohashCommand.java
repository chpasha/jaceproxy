package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 16:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartInfohashCommand extends StartCommand {

    private final String infohash;
    private final Iterable<Integer> fileIndexes;

    public StartInfohashCommand(String infohash, Iterable<Integer> fileIndexes) {
        super(Type.INFOHASH);
        this.infohash = infohash;
        this.fileIndexes = fileIndexes;
    }
}

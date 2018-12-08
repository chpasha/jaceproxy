package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoadAsyncInfohashCommand extends LoadAsyncCommand {

    private String infohash;

    public LoadAsyncInfohashCommand(String infohash, @Nullable Integer fileIndex) {
        super(Type.INFOHASH, fileIndex);
        this.infohash = infohash;
    }
}

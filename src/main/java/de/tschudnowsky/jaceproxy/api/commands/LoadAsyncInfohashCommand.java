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
public class LoadAsyncInfohashCommand extends LoadAsyncCommand {

    private String infohash;

    private LoadAsyncInfohashCommand(String infohash) {
        this();
        this.infohash = infohash;
    }

    public LoadAsyncInfohashCommand() {
        super(Type.INFOHASH);
    }
}

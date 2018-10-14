package de.tschudnowsky.jaceproxy.api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoadAsyncInfohashCommand extends LoadAsyncCommand {

    private String infohash;

    public LoadAsyncInfohashCommand(String infohash) {
        super(Type.INFOHASH);
        this.infohash = infohash;
    }


}

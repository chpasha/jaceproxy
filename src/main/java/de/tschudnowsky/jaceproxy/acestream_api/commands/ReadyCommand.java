package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReadyCommand extends CommandImpl {

    private String key;

    public ReadyCommand(String key) {
        super("READY");

        this.key = key;
    }
}

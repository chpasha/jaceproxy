package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 02.11.18
 * Time: 18:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SeekCommand extends CommandImpl {

    private int position;

    public SeekCommand(int position) {
        super("EVENT");
        this.position = position;
    }
}

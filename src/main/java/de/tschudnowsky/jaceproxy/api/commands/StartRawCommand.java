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
public class StartRawCommand extends StartCommand {

    private final String transportAsBase64;
    private final Iterable<Integer> fileIndexes;

    public StartRawCommand(String transportAsBase64, Iterable<Integer> fileIndexes) {
        super(Type.RAW);
        this.transportAsBase64 = transportAsBase64;
        this.fileIndexes = fileIndexes;
    }
}

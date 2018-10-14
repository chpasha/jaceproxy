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
public class StartPidCommand extends StartCommand {

    private final String contentId;
    private final Iterable<Integer> fileIndexes;

    public StartPidCommand(String contentId, Iterable<Integer> fileIndexes) {
        super(Type.PID);
        this.contentId = contentId;
        this.fileIndexes = fileIndexes;
    }
}

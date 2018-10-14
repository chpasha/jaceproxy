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
public class StartUrlCommand extends StartCommand {

    private final String url;
    private final Iterable<Integer> fileIndexes;

    public StartUrlCommand(String url, Iterable<Integer> fileIndexes) {
        super(Type.URL);
        this.url = url;
        this.fileIndexes = fileIndexes;
    }
}

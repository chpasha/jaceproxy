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
public class LoadAsyncContentIDCommand extends LoadAsyncCommand {

    private String contentId;

    public LoadAsyncContentIDCommand(String contentId) {
        super(Type.PID);
        this.contentId = contentId;
    }
}

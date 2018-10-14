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
public class LoadAsyncContentIDCommand extends LoadAsyncCommand {

    private String contentId;

    private LoadAsyncContentIDCommand(String contentId) {
        this();
        this.contentId = contentId;
    }

    public LoadAsyncContentIDCommand() {
        super(Type.PID);
    }
}

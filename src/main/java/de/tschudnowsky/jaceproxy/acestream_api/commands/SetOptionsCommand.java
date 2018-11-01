package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:53
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class SetOptionsCommand extends CommandImpl {

    private boolean notifyAboutDownloadStopped;

    private SetOptionsCommand(boolean notifyAboutDownloadStopped) {
        this();
        this.notifyAboutDownloadStopped = notifyAboutDownloadStopped;
    }

    public SetOptionsCommand() {
        super("SETOPTIONS");
    }

}

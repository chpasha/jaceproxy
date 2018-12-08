package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoadAsyncContentIDCommand extends LoadAsyncCommand {

    private String contentId;

    public LoadAsyncContentIDCommand(String contentId, @Nullable Integer fileIndex) {
        super(Type.PID, fileIndex);
        this.contentId = contentId;
    }
}

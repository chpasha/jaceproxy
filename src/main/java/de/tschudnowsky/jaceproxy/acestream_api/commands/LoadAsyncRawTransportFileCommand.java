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
public class LoadAsyncRawTransportFileCommand extends LoadAsyncCommand {

    private String transportFileAsBase64;

    public LoadAsyncRawTransportFileCommand(String transportFileAsBase64, @Nullable Integer fileIndex) {
        super(Type.RAW, fileIndex);
        this.transportFileAsBase64 = transportFileAsBase64;
    }
}

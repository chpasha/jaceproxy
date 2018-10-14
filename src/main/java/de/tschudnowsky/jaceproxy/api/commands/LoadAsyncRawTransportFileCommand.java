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
public class LoadAsyncRawTransportFileCommand extends LoadAsyncCommand {

    private String transportFileAsBase64;

    private LoadAsyncRawTransportFileCommand(String transportFileAsBase64) {
        this();
        this.transportFileAsBase64 = transportFileAsBase64;
    }

    public LoadAsyncRawTransportFileCommand() {
        super(Type.RAW);
    }
}

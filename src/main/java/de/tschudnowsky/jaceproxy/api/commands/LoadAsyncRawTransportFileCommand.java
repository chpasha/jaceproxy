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
public class LoadAsyncRawTransportFileCommand extends LoadAsyncCommand {

    private String transportFileAsBase64;

    public LoadAsyncRawTransportFileCommand(String transportFileAsBase64) {
        super(Type.RAW);
        this.transportFileAsBase64 = transportFileAsBase64;
    }
}

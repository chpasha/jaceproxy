package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 16:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class StartFileCommand extends StartCommand {

    private final String fileUrl;

    public StartFileCommand(String fileUrl) {
        super(Type.EFILE);
        this.fileUrl = urlEncode(fileUrl);
    }

    private String urlEncode(String fileUrl)  {
        try {
            return URLEncoder.encode(fileUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Encode file path", e);
        }
        return null;
    }
}

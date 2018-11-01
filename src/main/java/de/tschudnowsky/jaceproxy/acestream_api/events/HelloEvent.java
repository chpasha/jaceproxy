package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 13:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HelloEvent extends EventImpl {

    private String engineVersion;
    private String versionCode;
    private String requestKey;
    private Integer httpPort;
    private String bmode;

    public HelloEvent() {
    }
}

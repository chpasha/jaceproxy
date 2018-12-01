package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 19:22
 *
 * <<INFO 1;Cannot find active peers
 * <<INFO 2;Advertising video
 * <<INFO 3;Main content
 * <<INFO 0;Display some message
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InfoEvent extends EventImpl {

    private Integer code;
    private String description;
}

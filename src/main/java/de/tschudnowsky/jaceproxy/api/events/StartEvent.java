package de.tschudnowsky.jaceproxy.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 19:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartEvent extends EventImpl {
    private String url;
    private boolean isLiveStream;
}

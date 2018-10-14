package de.tschudnowsky.jaceproxy.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 14:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StatusEvent extends EventImpl {
    private String status;
}

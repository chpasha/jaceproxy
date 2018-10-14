package de.tschudnowsky.jaceproxy.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 19:22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StateEvent extends EventImpl {

    private Integer state;
    private String description;
}

package de.tschudnowsky.jaceproxy.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 19:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DownloadStoppedEvent extends EventImpl {
    private String reason;
    private String option;
}

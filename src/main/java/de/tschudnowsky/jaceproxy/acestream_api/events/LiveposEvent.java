package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 18:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LiveposEvent extends EventImpl {

    private String last;
    private String liveFirst;
    private String pos;
    private String firstTs;
    private String lastTs;
    private String liveLast;
    private boolean isLive;
    private Integer bufferPieces;
}

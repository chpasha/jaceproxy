package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 14:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StatusEvent extends EventImpl {

    //main:buf;9;0;0;0;1210;0;7;23;0;314294272;0;9650176
    //main:buf;9;0;0;0;2265;0;0;7;0;221609984;0;0
    //main:dl;0;0;1267;0;266;21;0;1207959552;0;94027776
    //main:dl;0;0;1102;0;201;29;0;2813296640;0;400228352
    //main:dl;0;0;2372;0;0;3;0;78790656;0;0
    private static final int PROGRESS = 0;
    private static final int PROGRESS_INTERMEDIATE = 1;
    private static final int DL_SPEED = 2;
    private static final int DL_SPEED_BUFFER = 4;
    private static final int HTTP_DL_SPEED = 3;
    private static final int UP_SPEED = 4;
    private static final int PEERS = 5;
    private static final int PEERS_BUFFER = 7;
    private static final int HTTP_PEERS = 6;
    private static final int DL_TOTAL = 7;
    private static final int HTTP_DL_TOTAL = 8;
    private static final int UP_TOTAL = 9;
    private static final int DOWNLOAD_TOTAL_BUFFER = 9;

    private String status;

    @Nullable
    public String getDescription() {
        String[] segments = status.split(";");
        switch (segments[0]) {
            case "main:buf":
                return buffering(ArrayUtils.subarray(segments, 1, segments.length));
            case "main:dl":
                return downloading(ArrayUtils.subarray(segments, 1, segments.length));

        }
        return null;
    }

    private String buffering(String[] segments) {
        return String.format("Buffering: %s%%, ⇩ %s Kb/s ( ∑ %,dMb ), Peers %s",
                segments[PROGRESS],
                segments[DL_SPEED_BUFFER], byteToMb(segments[DOWNLOAD_TOTAL_BUFFER]),
                segments[PEERS_BUFFER]);
    }

    private String downloading(String[] segments) {
        return String.format("Downloading: ⇩ %s Kb/s ( ∑ %,dMb ), ⇧ %s Kb/s ( ∑ %,dMb ), Peers %s",
                segments[DL_SPEED], byteToMb(segments[DL_TOTAL]),
                segments[UP_SPEED], byteToMb(segments[UP_TOTAL]),
                segments[PEERS]);
    }

    private long byteToMb(String value) {
        try {
            return Long.parseLong(value) / (1024 * 1024);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

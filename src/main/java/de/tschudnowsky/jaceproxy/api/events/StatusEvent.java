package de.tschudnowsky.jaceproxy.api.events;

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
    private String status;

    @Nullable
    public String getDescription() {
        //TODO simplify in one line e.g. Progress 10%, Down 1560kb/sec, Up 100kb/sec, Peers 5
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
        return String.format("Buffering: %s%%", segments[0]);
    }

    private String downloading(String[] segments) {
        return String.format("Downloading: ⇩ %s Kb/s ( ∑ %,dMb ), ⇧ %s Kb/s ( ∑ %,dMb ), Peers %s",
                 segments[2], byteToMb(segments[7]), segments[4], byteToMb(segments[9]), segments[5]);
    }

    private int byteToMb(String value) {
        try {
            return Integer.parseInt(value) / (1024 * 1024);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

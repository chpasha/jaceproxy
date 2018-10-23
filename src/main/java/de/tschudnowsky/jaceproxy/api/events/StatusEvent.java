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
                //TODO buffering has no peers
                return downloading(ArrayUtils.subarray(segments, 1, segments.length));
            case "main:dl":
                return downloading(ArrayUtils.subarray(segments, 1, segments.length));

        }
        return null;
    }

    private String downloading(String[] segments) {
        String newLine = "\r\n";
        return new StringBuilder(newLine)
                .append("Progress: ").append(segments[0]).append("%").append(newLine)
                .append("Download: ").append(segments[2]).append(" Kb/sec").append(newLine)
                .append("Upload: ").append(segments[4]).append(" Kb/sec").append(newLine)
                .append("Peers: ").append(segments[5]).append(newLine)
                //.append("Downloaded: ").append(toMb(segments[7])).append(newLine)
                //.append("Uploaded: ").append(toMb(segments[9])).append(newLine)
                .toString();
    }

    /*private Object toMb(String value) {
        try {
            float val = Float.parseFloat(value);
            return val / 1024;
        } catch (NumberFormatException e) {
            return value;
        }
    }*/
}

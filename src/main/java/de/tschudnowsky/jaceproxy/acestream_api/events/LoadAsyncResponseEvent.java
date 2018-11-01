package de.tschudnowsky.jaceproxy.acestream_api.events;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 14:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LoadAsyncResponseEvent extends EventImpl {

    private int requestId;

    private Response response;

    @Data
    public static class Response {

        private TransportFileContentDescription status;

        private List<TransportFile> files;

        private String infohash;

        private String checksum;
    }

    @Data
    @Builder
    public static class TransportFile {
        private String filename;
        private int streamId;
    }

    public enum TransportFileContentDescription {
        NO_AUDI_VIDEO(0),
        ONE_AUDIO_VIDEO(1),
        MULTIPLE_AUDIO_VIDEO(2),
        ERROR_RETRIEVING(100);

        private int value;

        TransportFileContentDescription(int value) {
            this.value = value;
        }

        @Nullable
        static TransportFileContentDescription fromValue(int value) {
            for (TransportFileContentDescription val : TransportFileContentDescription.values()) {
                if (val.value == value) {
                    return val;
                }
            }
            return null;
        }
    }
}

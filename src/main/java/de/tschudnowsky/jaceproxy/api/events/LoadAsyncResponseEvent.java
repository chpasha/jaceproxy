package de.tschudnowsky.jaceproxy.api.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
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

        @JsonProperty("status")
        private TransportFileContentDescription status;

        @JsonProperty("files")
        private List<TransportFile> files;

        @JsonProperty("infohash")
        private String infohash;

        @JsonProperty("checksum")
        private String checksum;
    }

    @Data
    @Builder
    @JsonDeserialize(using = TransportFileDeserializer.class)
    public static class TransportFile {
        private String filename;
        private int position;
    }

    public static class TransportFileDeserializer extends JsonDeserializer<TransportFile> {

        @Override
        public TransportFile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Iterator<ArrayList> values = p.readValuesAs(ArrayList.class);
            if (values.hasNext()) {
                ArrayList array = values.next();
                return TransportFile.builder()
                        .filename(URLDecoder.decode((String) array.get(0), "UTF-8"))
                        .position((Integer)array.get(1))
                        .build();
            }
            return null;
        }
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

        @JsonValue
        public int toValue() {
            return value;
        }
    }
}

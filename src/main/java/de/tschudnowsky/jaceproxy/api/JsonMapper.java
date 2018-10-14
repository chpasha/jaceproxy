package de.tschudnowsky.jaceproxy.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 14:25
 */
public class JsonMapper {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
              .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }
}

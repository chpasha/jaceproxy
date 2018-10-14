package de.tschudnowsky.jaceproxy.api.events;

import de.tschudnowsky.jaceproxy.api.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import static de.tschudnowsky.jaceproxy.api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 14:27
 */
@Slf4j
public class LoadAsyncResponseEventMapper implements EventMapper<LoadAsyncResponseEvent> {

    @Override
    public LoadAsyncResponseEvent readValue(String rawValue) {
        try {
            int requestId = Integer.parseInt(rawValue.substring(0, rawValue.indexOf(PROPERTY_SEPARATOR)));
            LoadAsyncResponseEvent.Response response = JsonMapper.MAPPER.readValue(
                    rawValue.substring(rawValue.indexOf("{")), LoadAsyncResponseEvent.Response.class
            );
            return LoadAsyncResponseEvent.builder()
                                         .requestId(requestId)
                                         .response(response)
                                         .build();
        } catch (Exception e) {
            log.error("Converting LoadAsyncResponse", e);
        }
        return null;
    }
}

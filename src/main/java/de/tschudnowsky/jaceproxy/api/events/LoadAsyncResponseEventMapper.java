package de.tschudnowsky.jaceproxy.api.events;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
            LoadAsyncResponseEvent.Response response = createFromString(rawValue.substring(rawValue.indexOf("{")));
            return LoadAsyncResponseEvent.builder()
                                         .requestId(requestId)
                                         .response(response)
                                         .build();
        } catch (Exception e) {
            log.error("Converting LoadAsyncResponse", e);
        }
        return null;
    }

    private LoadAsyncResponseEvent.Response createFromString(String value) throws UnsupportedEncodingException {
        JSONObject json = new JSONObject(value);
        LoadAsyncResponseEvent.Response r = new LoadAsyncResponseEvent.Response();
        r.setChecksum(json.getString("checksum"));
        r.setInfohash(json.getString("infohash"));
        r.setStatus(toStatus(json.getInt("status")));
        r.setFiles(toFiles(json.getJSONArray("files")));
        return r;
    }

    private LoadAsyncResponseEvent.TransportFileContentDescription toStatus(int status) {
        try {
            return LoadAsyncResponseEvent.TransportFileContentDescription.fromValue(status);
        } catch (IllegalArgumentException e) {
            log.error("Unknown TransportFileContentDescription  {}", status);
            return null;
        }
    }

    private List<LoadAsyncResponseEvent.TransportFile> toFiles(@Nullable JSONArray files) throws UnsupportedEncodingException {
        if (files != null && !files.isEmpty()) {
            List<LoadAsyncResponseEvent.TransportFile> result = new ArrayList<>();
            for (Object file : files) {
                JSONArray array = (JSONArray) file;
                result.add(LoadAsyncResponseEvent.TransportFile
                        .builder()
                        .filename(URLDecoder.decode((String) array.get(0), "UTF-8"))
                        .streamId((Integer) array.get(1))
                        .build()
                );
            }
            return result;
        } else
            return null;
    }
}

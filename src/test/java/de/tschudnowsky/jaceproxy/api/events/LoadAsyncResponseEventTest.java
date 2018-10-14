package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class LoadAsyncResponseEventTest extends EventMapperTest {

    @Test
    public void testReadValue() {
        String rawEvent = "LOADRESP 123123 {\"status\": 0, \"files\": [[\"%D0%9C%D0%B0%D1%82%D1%87%20%D0%A2%D0%92%20HD\", 0]], \"infohash\": \"112565f3e7359e738e34961ae24bcdefe17cf904\", \"checksum\": \"c77241f1ca0862ed88d4eee043ad7b8c3cc13a1f\"}";

        LoadAsyncResponseEvent event = createEvent(rawEvent);

        assertThat(event).isNotNull();
        assertThat(event.getRequestId()).isEqualTo(123123);
        LoadAsyncResponseEvent.Response response = event.getResponse();
        assertThat(response.getChecksum()).isNotBlank();
        assertThat(response.getInfohash()).isNotBlank();
        assertThat(response.getFiles())
                .hasSize(1)
                .contains(LoadAsyncResponseEvent.TransportFile.builder()
                                                              .filename("Матч ТВ HD")
                                                              .position(0)
                                                              .build());


    }
}

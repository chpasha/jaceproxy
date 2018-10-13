package de.tschudnowsky.jaceproxy.api.events;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 10:45
 */
public class HelloEventTest extends EventMapperTest {

    @Test
    public void testReadValue() {
        String engineVersion = "1";
        String versionCode = "2";
        String requestKey = UUID.randomUUID().toString();
        Integer httpPort = 8080;
        String rawEvent = String.format("HELLOTS version=%s version_code=%s key=%s http_port=%s",
                engineVersion, versionCode, requestKey, httpPort);

        HelloEvent hello = createEvent(rawEvent);

        assertThat(hello).isNotNull();
        assertThat(hello.getEngineVersion()).isEqualTo(engineVersion);
        assertThat(hello.getHttpPort()).isEqualTo(httpPort);
        assertThat(hello.getRequestKey()).isEqualTo(requestKey);
        assertThat(hello.getVersionCode()).isEqualTo(versionCode);

    }
}

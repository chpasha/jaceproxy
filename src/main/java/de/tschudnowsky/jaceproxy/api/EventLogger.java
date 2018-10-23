package de.tschudnowsky.jaceproxy.api;

import de.tschudnowsky.jaceproxy.api.events.Event;
import de.tschudnowsky.jaceproxy.api.events.StatusEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 14:09
 *
 * Log interesting acestream events here
 */
@ChannelHandler.Sharable
@Slf4j
public class EventLogger extends MessageToMessageDecoder<Event> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Event msg, List<Object> out) throws Exception {
        if (msg instanceof StatusEvent) {
            logState(((StatusEvent) msg));
        }
        out.add(msg);
    }

    private void logState(StatusEvent msg) {
        String description = msg.getDescription();
        if (isNotBlank(description)) {
            log.info(description);
        }
    }
}

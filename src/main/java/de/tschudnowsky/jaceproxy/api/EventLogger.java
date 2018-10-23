package de.tschudnowsky.jaceproxy.api;

import de.tschudnowsky.jaceproxy.api.events.Event;
import de.tschudnowsky.jaceproxy.api.events.StateEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
        if (msg instanceof StateEvent) {
            logState(((StateEvent) msg));
        }
        out.add(msg);
    }

    private void logState(StateEvent msg) {
        log.info(msg.getDescription());
    }
}

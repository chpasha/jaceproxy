package de.tschudnowsky.jaceproxy.acestream_api;

import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.StatusEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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
public class EventLogger extends SimpleChannelInboundHandler<Event> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) {
        if (msg instanceof StatusEvent) {
            logState(((StatusEvent) msg));
        }
        ctx.fireChannelRead(msg);
    }

    private void logState(StatusEvent msg) {
        String description = msg.getDescription();
        if (isNotBlank(description)) {
            log.debug(description);
        }
    }
}

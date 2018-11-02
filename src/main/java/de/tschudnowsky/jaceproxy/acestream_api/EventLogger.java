package de.tschudnowsky.jaceproxy.acestream_api;

import de.tschudnowsky.jaceproxy.acestream_api.events.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 14:09
 * <p>
 * Log interesting acestream events here
 */
@ChannelHandler.Sharable
@Slf4j
public class EventLogger extends SimpleChannelInboundHandler<Event> {

    private static final List<Class> discardEvents = asList(ResumeEvent.class, PauseEvent.class, LiveposEvent.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) {
        if (discardEvents.contains(msg.getClass())) {
            log.trace("Discarding useless event {}", msg);
        } else if (msg instanceof StatusEvent) {
            logState(((StatusEvent) msg));
        } else {
            if (msg instanceof HelloEvent) {
                log.info("Connected to acestream engine version {}", ((HelloEvent) msg).getEngineVersion());
            }
            ctx.fireChannelRead(msg);
        }
    }

    private void logState(StatusEvent msg) {
        String description = msg.getDescription();
        if (isNotBlank(description)) {
            log.debug(description);
        }
    }
}

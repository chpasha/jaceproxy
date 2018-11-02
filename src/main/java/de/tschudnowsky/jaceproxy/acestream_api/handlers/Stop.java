package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.acestream_api.events.StopEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * User: pavel
 * Date: 02.11.18
 * Time: 18:04
 */
@Slf4j
public class Stop extends SimpleChannelInboundHandler<StopEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StopEvent msg) throws Exception {
        log.warn("Received stop event from acestream engine, stopping and notifying clients");
        ctx.close();
    }
}

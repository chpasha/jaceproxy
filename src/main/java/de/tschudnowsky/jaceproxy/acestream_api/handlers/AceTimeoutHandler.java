package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.acestream_api.commands.ShutdownCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 14:09
 * <p>
 * Log interesting acestream events here
 */
@ChannelHandler.Sharable
@Slf4j
@RequiredArgsConstructor
public class AceTimeoutHandler extends SimpleChannelInboundHandler<Event> {

    @NonNull
    private final Channel inboundChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            log.error("Timeout receiving response from ace engine, exiting");
            inboundChannel.close();
            ctx.writeAndFlush(new StopCommand());
            ctx.writeAndFlush(new ShutdownCommand());
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}

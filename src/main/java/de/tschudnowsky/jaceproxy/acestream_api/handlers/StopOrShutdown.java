package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.acestream_api.commands.ShutdownCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.StopEvent;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User: pavel
 * Date: 02.11.18
 * Time: 18:04
 */
@Slf4j
@RequiredArgsConstructor
public class StopOrShutdown extends SimpleChannelInboundHandler<Event> {

    private final ChannelGroup playerChannelGroup;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) {
        if (msg instanceof StopEvent) /*TODO ShutDownEvent*/ {
            log.warn("Received stop event from acestream engine, stopping and notifying clients");
            playerChannelGroup.close();
            ctx.close().syncUninterruptibly();
        } else if (playerChannelGroup.isEmpty()) {
            log.warn("All clients of the group {} disconnected, stopping ace session", playerChannelGroup.name());
            shutdownAceClient(ctx);
        }
    }

    private void shutdownAceClient(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new StopCommand());
        ctx.writeAndFlush(new ShutdownCommand())
           .addListener(ChannelFutureListener.CLOSE);
    }
}

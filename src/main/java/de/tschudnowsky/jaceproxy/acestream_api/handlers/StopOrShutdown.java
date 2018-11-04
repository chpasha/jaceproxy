package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.JAceHttpServer;
import de.tschudnowsky.jaceproxy.acestream_api.commands.ShutdownCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.StopEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static de.tschudnowsky.jaceproxy.acestream_api.AceStreamClientInitializer.STREAM_OWNER;

/**
 * User: pavel
 * Date: 02.11.18
 * Time: 18:04
 */
@Slf4j
public class StopOrShutdown extends SimpleChannelInboundHandler<Event> {

    private final Channel inboundChannel;
    private static Channel streamOwnerChannel;

    public StopOrShutdown(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        inboundChannel.closeFuture().addListener(future -> onClientDisconnected(ctx));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) {
        if (msg instanceof StopEvent) {
            log.warn("Received stop event from acestream engine, stopping and notifying clients");
            ctx.close();
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void onClientDisconnected(ChannelHandlerContext ctx) {
        log.info("Client {} disconnected", inboundChannel.id());
        if (JAceHttpServer.isLastChannelInGroup(inboundChannel.id())) {
            log.info("It was the last client, stopping acestream broadcast");
            shutdownAceClient(ctx);
        } else if (ctx.channel().hasAttr(STREAM_OWNER) && ctx.channel().attr(STREAM_OWNER).get()) {
            log.info("This channel started broadcast and cannot be closed as long as other channels are subscribed");
            streamOwnerChannel = ctx.channel();
        } else {
            ctx.close();
        }
    }

    private void shutdownAceClient(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new StopCommand());
        ctx.writeAndFlush(new ShutdownCommand());
        if (streamOwnerChannel != null) {
            streamOwnerChannel.close();
            streamOwnerChannel = null;
        }
        ctx.close();
    }
}

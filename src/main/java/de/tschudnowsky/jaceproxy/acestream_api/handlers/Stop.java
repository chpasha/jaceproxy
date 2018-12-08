package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.acestream_api.commands.ShutdownCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.InfoEvent;
import de.tschudnowsky.jaceproxy.acestream_api.events.ShutdownEvent;
import de.tschudnowsky.jaceproxy.acestream_api.events.StopEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * User: pavel
 * Date: 02.11.18
 * Time: 18:04
 */
@Slf4j
@RequiredArgsConstructor
public class Stop extends SimpleChannelInboundHandler<Event> implements GenericFutureListener<Future<Void>> {

    private final ChannelGroup playerChannelGroup;
    private boolean isRemovedFromPipeline;

    //if we initiate shutdown, we'll get Shutdown-Event as answer and have to ignore it
    //otherwise we should really shutdown
    private boolean iDidNotInitiatedShutdown = true;
    
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        this.ctx = ctx;
        playerChannelGroup.newCloseFuture().removeListener(this);
        playerChannelGroup.newCloseFuture().addListener(this);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        playerChannelGroup.newCloseFuture().removeListener(this);
        isRemovedFromPipeline = true;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) {
        if (msg instanceof InfoEvent) {
            InfoEvent info = (InfoEvent) msg;
            if (Objects.equals(info.getCode(), 1)) {
                log.warn("Ace Engine could not find any peers, stopping and notifying clients");
                closeContextAndClients(ctx);
                shutdownAceClient(ctx);
            }
        }
        if (msg instanceof StopEvent) {
            log.warn("Received stop event from acestream engine, stopping and notifying clients");
            closeContextAndClients(ctx);
        }
        if (msg instanceof ShutdownEvent) {

            if (iDidNotInitiatedShutdown) {
                log.warn("Received shutdown event from acestream engine, stopping and notifying clients");
                closeContextAndClients(ctx);
            } else {
                ctx.close();
            }
        }
    }

    private void closeContextAndClients(ChannelHandlerContext ctx) {
        playerChannelGroup.close();
        ctx.close().syncUninterruptibly();
    }

    @Override
    public void operationComplete(Future<Void> future) {
        if (isRemovedFromPipeline) {
            return;
        }

        // This event is fired when all channels in the group that existed at subscription moment were closed
        // but it is possible that new clients have joined same broadcast and new channels were added to group
        // after that so we have to check if group is empty and if not, subscribe again instead of closing

        if (playerChannelGroup.isEmpty()) {
            log.warn("All clients of the group {} disconnected, stopping ace session", playerChannelGroup.name());
            shutdownAceClient(ctx);
        } else {
            log.warn("Original client disconnected but channel group {} is not empty, continue work", playerChannelGroup.name());
            playerChannelGroup.newCloseFuture().removeListener(this);
            playerChannelGroup.newCloseFuture().addListener(this);
        }
    }

    private void shutdownAceClient(ChannelHandlerContext ctx) {
        iDidNotInitiatedShutdown = false;
        ctx.writeAndFlush(new StopCommand());
        ctx.writeAndFlush(new ShutdownCommand());
    }
}

package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.acestream_api.commands.ShutdownCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.ShutdownEvent;
import de.tschudnowsky.jaceproxy.acestream_api.events.StopEvent;
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
    private boolean isRemovedFromPipeline;

    //if we initiate shutdown, we'll get Shutdown-Event as answer and have to ignore it
    //otherwise we should really shutdown
    private boolean iDidNotInitiatedShutdown = true;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        playerChannelGroup.newCloseFuture().addListener(future -> onSomeClientsDisconnected(ctx));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        isRemovedFromPipeline = true;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) {
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

    private void onSomeClientsDisconnected(ChannelHandlerContext ctx) {
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
            playerChannelGroup.newCloseFuture().addListener(newFuture -> onSomeClientsDisconnected(ctx));
        }

    }

    private void shutdownAceClient(ChannelHandlerContext ctx) {
        iDidNotInitiatedShutdown = false;
        ctx.writeAndFlush(new StopCommand());
        ctx.writeAndFlush(new ShutdownCommand());
    }
}

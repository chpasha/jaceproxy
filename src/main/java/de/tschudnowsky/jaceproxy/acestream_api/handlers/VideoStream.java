/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.JAceConfig;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;


/**
 *
 */
@Sharable
@Slf4j
public class VideoStream extends SimpleChannelInboundHandler<HttpObject> implements GenericFutureListener<Future<Void>> {

    private final ChannelGroup clients;
    private final Start startHandler;
    private final boolean sendLastHttpContentToClients; //we should not send LastHttp while streaming hls

    //we should always notify clients on channel-deactivate unless we got timeout
    //in this case clients should wait for restart
    private boolean shouldCloseClients = true;
    private ChannelHandlerContext ctx;


    VideoStream(@NotNull ChannelGroup clients, @NotNull Start startHandler) {
        this(clients, startHandler, true);
    }

    VideoStream(@NotNull ChannelGroup clients, @NotNull Start startHandler, boolean sendLastHttpContentToClients) {
        super(false);
        this.startHandler = startHandler;
        this.clients = clients;
        this.sendLastHttpContentToClients = sendLastHttpContentToClients;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        this.ctx = ctx;
        clients.newCloseFuture().removeListener(this);
        clients.newCloseFuture().addListener(this);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        clients.newCloseFuture().removeListener(this);
    }

    @Override
    public void operationComplete(Future<Void> future) {
        if (clients.isEmpty()) {
            shouldCloseClients = false;
            ctx.close();
        } else {
            clients.newCloseFuture().removeListener(this);
            clients.newCloseFuture().addListener(this);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        log.info("Acestream closed connection, stopping download");
        if (shouldCloseClients) {
            log.info("Notifying clients of streaming end");
            clients.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {
            ReferenceCountUtil.release(msg);
        }
        if (msg instanceof HttpContent) {
            streamHttpContent((HttpContent) msg, ctx);
        }
    }


    private void streamHttpContent(HttpContent msg, ChannelHandlerContext ctx) {
        if (msg instanceof LastHttpContent) {
            if (sendLastHttpContentToClients) {
                log.warn("Reached end of stream");
                clients.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            }
            shouldCloseClients = false;
            ctx.close();
            msg.release();
        } else {
            clients.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            log.error("Timeout while streaming content");
            if (JAceConfig.INSTANCE.getRestartOnTimeout()) {
                shouldCloseClients = false;
                startHandler.onReadTimeoutWhileStreaming();
            } else {
                shouldCloseClients = true;
            }
        } else {
            log.error("Streaming failed", cause);
            clients.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        ctx.close();
    }
}

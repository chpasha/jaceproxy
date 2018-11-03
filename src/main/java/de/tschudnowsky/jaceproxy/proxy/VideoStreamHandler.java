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
package de.tschudnowsky.jaceproxy.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


/**
 *
 */
@Sharable
@Slf4j
public class VideoStreamHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Channel playerChannel;

    VideoStreamHandler(Channel playerChannel) {
        super(false);
        this.playerChannel = playerChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final ChannelFutureListener listener = future -> inboundChannelClosed(ctx);
        // We are getting here multiple times if restarted on timeout when parent channel closed,
        // so we must register listener on channelActive
        // and unregister every time our context is closed - or we get multiple notifications which is not
        // a tragedy but still not good
        playerChannel.closeFuture().addListener(listener);
        ctx.channel().closeFuture().addListener(future -> playerChannel.closeFuture().removeListener(listener));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (playerChannel.isActive()) {
            if (msg instanceof HttpResponse) {
                logHttpResponse((HttpResponse) msg);
            }
            if (msg instanceof HttpContent) {
                streamHttpContent((HttpContent) msg, ctx);
            }
        } else {
            inboundChannelClosed(ctx);
        }
    }

    private void inboundChannelClosed(ChannelHandlerContext ctx) {
        log.warn("Player channel closed, stopping streaming");
        ctx.close();
    }

    private void logHttpResponse(HttpResponse msg) {
        try {
            log.debug("STATUS: {}", msg.status());
            log.debug("VERSION: {}", msg.protocolVersion());
            if (!msg.headers().isEmpty()) {
                for (CharSequence name : msg.headers().names()) {
                    for (CharSequence value : msg.headers().getAll(name)) {
                        log.debug("HEADER: {} = {}", name, value);
                    }
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void streamHttpContent(HttpContent msg, ChannelHandlerContext ctx) {
        if (msg instanceof LastHttpContent) {
            log.info("Download stopped");
            playerChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            msg.release();
            ctx.close();
        } else {
            playerChannel.writeAndFlush(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException && playerChannel.isActive()) {
            playerChannel.pipeline().get(HttpHandler.class).onReadTimeoutWhileStreaming(playerChannel);
        } else {
            log.error("Playback failed", cause);
            if (playerChannel.isActive()) {
                playerChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            }
        }
        ctx.close();
    }
}

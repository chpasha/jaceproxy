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

import de.tschudnowsky.jaceproxy.JAceHttpServer;
import de.tschudnowsky.jaceproxy.proxy.HttpHandler;
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
import lombok.extern.slf4j.Slf4j;


/**
 *
 */
@Sharable
@Slf4j
public class VideoStreamHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final String infohash;
    private ChannelGroup playerChannelGroup;

    VideoStreamHandler(String infohash) {
        super(false);
        this.infohash = infohash;
        this.playerChannelGroup = JAceHttpServer.getOrCreateChannelGroup(infohash);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //TODO respond to channels closed
        /*final ChannelFutureListener listener = future -> inboundChannelClosed(ctx);
        // We are getting here multiple times if restarted on timeout when parent channel closed,
        // so we must register listener on channelActive
        // and unregister every time our context is closed - or we get multiple notifications which is not
        // a tragedy but still not good
        playerChannel.closeFuture().addListener(listener);
        ctx.channel().closeFuture().addListener(future -> playerChannel.closeFuture().removeListener(listener));*/
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (playerChannelGroup.isEmpty()) {
            inboundChannelClosed(ctx);
        } else {
            if (msg instanceof HttpResponse) {
                logHttpResponse((HttpResponse) msg);
            }
            if (msg instanceof HttpContent) {
                streamHttpContent((HttpContent) msg, ctx);
            }
        }
    }

    private void inboundChannelClosed(ChannelHandlerContext ctx) {
        //TODO only if no channels in group
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
            playerChannelGroup.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            msg.release();
            ctx.close();
        } else {
            playerChannelGroup.writeAndFlush(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            //TODO test this
            //Restart only one handler for Handshake/Load etc.
            playerChannelGroup
                    .stream()
                    .findAny()
                    .ifPresent(playerChannel -> playerChannel.pipeline().get(HttpHandler.class).onReadTimeoutWhileStreaming(playerChannel));
        } else {
            log.error("Playback failed", cause);
            playerChannelGroup.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        ctx.close();
    }
}

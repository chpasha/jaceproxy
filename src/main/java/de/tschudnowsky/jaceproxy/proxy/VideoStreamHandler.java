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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedInput;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 *
 */
@Sharable
@Slf4j
@RequiredArgsConstructor
public class VideoStreamHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Channel playerChannel;

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
                sendHttpResponse((HttpResponse) msg);
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

    private void sendHttpResponse(HttpResponse msg) {
        HttpResponse response = msg;
        log.debug("STATUS: {}", response.status());
        log.debug("VERSION: {}", response.protocolVersion());
        if (!response.headers().isEmpty()) {
            for (CharSequence name : response.headers().names()) {
                for (CharSequence value : response.headers().getAll(name)) {
                    log.debug("HEADER: {} = {}", name, value);
                }
            }
        }
        response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(TRANSFER_ENCODING, CHUNKED);
        response.headers().set(HttpHeaderNames.CONNECTION, KEEP_ALIVE);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_OCTET_STREAM);
        response.headers().set(HttpHeaderNames.ACCEPT_RANGES, BYTES);
        playerChannel.writeAndFlush(response);
    }

    private void streamHttpContent(HttpContent msg, ChannelHandlerContext ctx) {
        if (msg instanceof LastHttpContent) {
            log.info("Download stopped");
            playerChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            ctx.close();
        } else {
            ChunkedInput<ByteBuf> chunkedInput = new ChunkedStream(new ByteBufInputStream(msg.content())) {
                // Seems like HttpContent is always released, so when channel closed we get
                // IllegalReferenceCountException: refCnt in ChunkedInput.isEndOfInput()
                // as workaround, if channel closed, just report true
                @Override
                public boolean isEndOfInput() throws Exception {
                    if (playerChannel.isActive()) {
                        return super.isEndOfInput();
                    } else {
                        return true;
                    }
                }
            };
            playerChannel.writeAndFlush(chunkedInput);
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

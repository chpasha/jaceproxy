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
package de.tschudnowsky.jaceproxy.handlers;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
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
public class Download extends SimpleChannelInboundHandler<HttpObject> {

    private final Channel inboundChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

            log.info("STATUS: {}", response.status());
            log.info("VERSION: {}", response.protocolVersion());

            if (!response.headers().isEmpty()) {
                for (CharSequence name : response.headers().names()) {
                    for (CharSequence value : response.headers().getAll(name)) {
                        log.info("HEADER: {} = {}", name, value);
                    }
                }
            }
            response = new DefaultHttpResponse(HTTP_1_1, OK);
            response.headers().set(TRANSFER_ENCODING, CHUNKED);
            response.headers().set(HttpHeaderNames.CONNECTION, KEEP_ALIVE);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_OCTET_STREAM);
            response.headers().set(HttpHeaderNames.ACCEPT_RANGES, BYTES);
            // Write the initial line and the header.
            inboundChannel.writeAndFlush(response);
        }
        if (msg instanceof HttpContent) {
            HttpContent chunk = (HttpContent) msg;

            HttpChunkedInput httpChunkWriter = new HttpChunkedInput(new ChunkedStream(
                    new ByteBufInputStream(chunk.content())
            ));
            inboundChannel.write(httpChunkWriter);

            if (chunk instanceof LastHttpContent) {
                log.info("Download stopped");
                inboundChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                inboundChannel.pipeline().get(ChunkedWriteHandler.class).resumeTransfer();
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Playback failed", cause);
        inboundChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        ctx.close();
    }
}

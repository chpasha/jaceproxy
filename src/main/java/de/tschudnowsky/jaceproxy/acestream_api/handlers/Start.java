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

import de.tschudnowsky.jaceproxy.acestream_api.commands.ShutdownCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StartCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.StartPlayEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.SocketUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;


/**
 *
 */
@Sharable
@Slf4j
@RequiredArgsConstructor
public class Start extends SimpleChannelInboundHandler<Event> {

    @NonNull
    private final StartCommand startCommand;

    @NonNull
    private final Channel inboundChannel;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        inboundChannel.closeFuture().addListener((ChannelFutureListener) future -> {
            log.warn("Inbound channel closed, stopping ace client");
            stopAceClient(ctx);
        });
        ctx.writeAndFlush(startCommand).sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event) {
        if (event instanceof StartPlayEvent) {
            StartPlayEvent startPlay = (StartPlayEvent) event;
            ctx.pipeline().remove(this);
            stream(ctx, startPlay.getUrl());
        }
    }

    private void stream(ChannelHandlerContext ctx, String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort();

            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop())
             .channel(ctx.channel().getClass())
             .handler(new ChannelInitializer<SocketChannel>() {

                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     pipeline.addLast(new HttpClientCodec())
                             //todo test
                             .addLast(new ReadTimeoutHandler(30))
                             .addLast(new Download(inboundChannel));
                 }
             })
            //TODO test
            //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ??)
            //.option(ChannelOption.SO_TIMEOUT, ??)
            ;
            ChannelFuture f = b.connect(SocketUtils.socketAddress(host, port));
            //Channel channel = f.sync().channel();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
                    f.channel().writeAndFlush(request);
                } else {
                    f.channel().close();
                    log.error("Failed to download {}", url);
                    stopAceClient(ctx);
                }
            });
        } catch (Exception e) {
            log.error("", e);
            stopAceClient(ctx);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("", cause);
        if (cause instanceof ReadTimeoutException) {

        }
        stopAceClient(ctx);
    }

    private void stopAceClient(ChannelHandlerContext ctx) {
        ctx.write(new StopCommand());
        ctx.write(new ShutdownCommand());
        ctx.flush();
        ctx.close();
    }
}

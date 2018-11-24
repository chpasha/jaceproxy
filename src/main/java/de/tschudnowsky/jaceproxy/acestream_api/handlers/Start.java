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
import de.tschudnowsky.jaceproxy.acestream_api.commands.StartCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.StartPlayEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.SocketUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;


/**
 *
 */
@Sharable
@Slf4j
public class Start extends SimpleChannelInboundHandler<Event> {

    @NonNull
    private final StartCommand startCommand;

    @NonNull
    private final Channel inboundChannel;

    private ChannelGroup playerChannelGroup;

    public Start(StartCommand startCommand, Channel inboundChannel, String infohash) {
        this.startCommand = startCommand;
        this.inboundChannel = inboundChannel;
        this.playerChannelGroup = JAceHttpServer.getOrCreateChannelGroup(infohash);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(startCommand);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event)  {
        if (event instanceof StartPlayEvent) {
            StartPlayEvent startPlay = (StartPlayEvent) event;
            ctx.pipeline().remove(this);
            ctx.pipeline().addLast(new StopOrShutdown(playerChannelGroup));
            ctx.fireChannelActive();
            streamUrl(ctx, startPlay.getUrl());
        }
    }

    private void streamUrl(ChannelHandlerContext ctx, String url) {
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
                             .addLast(new ReadTimeoutHandler(45))
                             .addLast(new VideoStream(playerChannelGroup));
                 }
             });
            ChannelFuture streamChannel = b.connect(SocketUtils.socketAddress(host, port));
            streamChannel.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
                    streamChannel.channel().writeAndFlush(request);
                } else {
                    streamChannel.channel().close();
                    log.error("Failed to download {}", uri.toString());
                }
            });

        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("", cause);
    }
}

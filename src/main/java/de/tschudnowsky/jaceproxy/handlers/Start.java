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

import de.tschudnowsky.jaceproxy.api.commands.StartTorrentCommand;
import de.tschudnowsky.jaceproxy.api.events.Event;
import de.tschudnowsky.jaceproxy.api.events.LoadAsyncResponseEvent;
import de.tschudnowsky.jaceproxy.api.events.StartPlayEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.SocketUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import static java.util.Collections.singletonList;


/**
 *
 */
@Sharable
@Slf4j
@RequiredArgsConstructor
public class Start extends SimpleChannelInboundHandler<Event> {

    @NonNull
    private final LoadAsyncResponseEvent.Response loadAsyncResponse;

    @NonNull
    private final String url;

    private final Channel inboundChannel;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoadAsyncResponseEvent.TransportFile transportFile = loadAsyncResponse.getFiles().get(0);
        StartTorrentCommand startCommand = new StartTorrentCommand(url, singletonList(0), transportFile.getStreamId());
        ctx.writeAndFlush(startCommand)
           .sync();
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
        //EventLoopGroup group = new NioEventLoopGroup();
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
                             .addLast(new Download(inboundChannel));
                 }
             });
            ChannelFuture f = b.connect(SocketUtils.socketAddress(host, port));
            //Channel channel = f.sync().channel();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
                    f.channel().writeAndFlush(request);
                } else {
                    f.channel().close();
                }
            });
            // Wait until the connection attempt succeeds or fails.
            // Prepare the HTTP request.

            // send request
            //f.channel().writeAndFlush(request);
            //channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("", e);
            ctx.close();
        } finally {
            //group.shutdownGracefully();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Start Failed failed", cause);
        ctx.close();
    }
}

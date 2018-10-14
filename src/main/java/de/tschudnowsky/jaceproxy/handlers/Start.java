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
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
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

            inboundChannel.writeAndFlush(startPlay.getUrl());


            if (true)
            return;

            EventLoopGroup group = new NioEventLoopGroup();
            try {

                URI uri = new URI(startPlay.getUrl());
                String host = uri.getHost();
                int port = uri.getPort();

                Bootstrap b = new Bootstrap();
                b.group(group)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<SocketChannel>() {

                     @Override
                     protected void initChannel(SocketChannel ch) {
                         ChannelPipeline pipeline = ch.pipeline();
                         pipeline.addLast(new HttpClientCodec());
                         pipeline.addLast(new ChunkedWriteHandler());
                         pipeline.addLast(new Download(startPlay.getUrl(), inboundChannel));
                     }
                 });


                Channel channel = b.connect(SocketUtils.socketAddress(host, port)).channel();
                // Wait until the connection attempt succeeds or fails.
                // Prepare the HTTP request.
                HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
                // send request
                channel.writeAndFlush(request);
                // Wait for the server to close the connection.
                channel.closeFuture().sync();

            } catch (Exception e) {
                log.error("", e);
                ctx.close();
            } finally {
                // Shut down executor threads to exit.
                group.shutdownGracefully();

                // Really clean all temporary files if they still exist
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Start Failed failed", cause);
        ctx.close();
    }
}

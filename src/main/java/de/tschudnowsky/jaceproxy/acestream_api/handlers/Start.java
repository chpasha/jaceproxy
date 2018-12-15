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

import de.tschudnowsky.jaceproxy.acestream_api.commands.StartCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StopCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.StartPlayEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.SocketUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;


/**
 *
 */
@Sharable
@Slf4j
public class Start extends SimpleChannelInboundHandler<Event> {

    @NonNull
    private final StartCommand startCommand;

    @NonNull
    private ChannelGroup playerChannelGroup;

    private ChannelHandlerContext ctx;

    public Start(@NonNull StartCommand startCommand, @NonNull ChannelGroup group) {
        this.startCommand = startCommand;
        this.playerChannelGroup = group;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //we add stop handler before start command to be able to respond to any unexpected events like no peers found
        ctx.pipeline().addLast(new Stop(playerChannelGroup));
        ctx.writeAndFlush(startCommand);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event) {
        this.ctx = ctx; //TODO is it ok to store context? theoretically there is only 1 Start instance per broadcast
        if (event instanceof StartPlayEvent) {
            StartPlayEvent startPlay = (StartPlayEvent) event;

            if (startPlay.getUrl().contains("m3u")) {
                streamPlaylist(ctx, startPlay.getUrl());
            } else {
                streamUrl(ctx, startPlay.getUrl());
            }
        }
    }

    /*
       Sometimes we get m3u hls as response instead of video stream url, so we have to stream it entry by entry
     */
    private void streamPlaylist(ChannelHandlerContext ctx, String url) {
        try {
            Bootstrap b = new Bootstrap();
            b.group(playerChannelGroup.iterator().next().eventLoop())
             .channel(ctx.channel().getClass())
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch)  {
                     ch.pipeline()
                       .addLast(new HttpClientCodec())
                       .addLast(HttpResponseLogger.INSTANCE)
                       .addLast(new PlaylistStream(playerChannelGroup, Start.this));
                 }
             });

            sendGET(b, url);
        } catch (URISyntaxException e) {
            log.error("", e);
        }
    }

    private void streamUrl(ChannelHandlerContext ctx, String url) {
        try {

            Bootstrap b = new Bootstrap();
            b.group(playerChannelGroup.iterator().next().eventLoop())
             .channel(ctx.channel().getClass())
             .handler(new HttpStreamInitializer(new VideoStream(playerChannelGroup, Start.this)));
            sendGET(b, url);

        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void sendGET(Bootstrap b, String url) throws URISyntaxException {
        URI uri = new URI(url);
        String host = uri.getHost();
        int port = uri.getPort();
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
    }

    synchronized void onReadTimeoutWhileStreaming() {
        log.info("Restarting broadcast");
        Stop stopOrShutdown = ctx.pipeline().get(Stop.class);
        if (stopOrShutdown != null) {
            ctx.pipeline().remove(stopOrShutdown);
        }
        ctx.writeAndFlush(new StopCommand());
        this.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("", cause);
    }
}

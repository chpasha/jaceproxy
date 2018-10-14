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
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoadAsyncResponseEvent.TransportFile transportFile = loadAsyncResponse.getFiles().get(0);
        StartTorrentCommand startCommand = new StartTorrentCommand(url, singletonList(0), transportFile.getStreamId());
        ctx.writeAndFlush(startCommand)
           .sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event) {


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Start Failed failed", cause);
        ctx.close();
    }
}

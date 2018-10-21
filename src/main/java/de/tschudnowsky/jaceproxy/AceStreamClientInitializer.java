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
package de.tschudnowsky.jaceproxy;

import de.tschudnowsky.jaceproxy.api.CommandEncoder;
import de.tschudnowsky.jaceproxy.api.EventDecoder;
import de.tschudnowsky.jaceproxy.handlers.Handshake;
import de.tschudnowsky.jaceproxy.handlers.LoadAsync;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class AceStreamClientInitializer extends ChannelInitializer<SocketChannel> {

    private final DelimiterBasedFrameDecoder TELNET_MESSAGE_DECODER = new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter());
    private final CommandEncoder COMMAND_ENCODER = new CommandEncoder(Charset.forName("US-ASCII"));
    private final EventDecoder EVENT_DECODER = new EventDecoder(Charset.forName("US-ASCII"));

    private final String url;
    private final Channel inboundChannel;

    @Override
    public void initChannel(SocketChannel ch) {

        ch.pipeline()
          .addLast(COMMAND_ENCODER)
          .addLast(TELNET_MESSAGE_DECODER)
          .addLast(EVENT_DECODER)
          .addLast(new Handshake())
          .addLast(new LoadAsync(url, inboundChannel))
        ;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        JAceHttpHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("", cause);
        JAceHttpHandler.closeOnFlush(ctx.channel());
    }
}

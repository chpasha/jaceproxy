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
package de.tschudnowsky.jaceproxy.acestream_api;

import de.tschudnowsky.jaceproxy.acestream_api.commands.LoadAsyncCommand;
import de.tschudnowsky.jaceproxy.acestream_api.handlers.EventLogger;
import de.tschudnowsky.jaceproxy.acestream_api.handlers.Handshake;
import de.tschudnowsky.jaceproxy.acestream_api.handlers.LoadAsync;
import io.netty.channel.Channel;
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

    private static final String TELNET_ENCODING = "US-ASCII";

    private final LoadAsyncCommand loadCommand;
    private final Channel inboundChannel;

    @Override
    public void initChannel(SocketChannel ch) {

        ch.pipeline()
          // --- encoders ---
          .addLast(new CommandEncoder(Charset.forName("US-ASCII")))
          // --- decoders ---
          .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
          .addLast(new EventDecoder(Charset.forName(TELNET_ENCODING)))
          // --- handlers ---
          .addLast(new EventLogger())
          .addLast(new Handshake())
          .addLast(new LoadAsync(loadCommand, inboundChannel))
        ;
    }
}

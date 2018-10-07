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

import de.tschudnowsky.jaceproxy.acestream.Handshake;
import de.tschudnowsky.jaceproxy.api.CommandEncoder;
import de.tschudnowsky.jaceproxy.api.EventDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

import java.nio.charset.Charset;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class TelnetClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final CommandEncoder COMMAND_ENCODER = new CommandEncoder(Charset.forName("US-ASCII"));
    private static final DelimiterBasedFrameDecoder TELNET_MESSAGE_DECODER = new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter());
    private  static final EventDecoder EVENT_DECODER = new EventDecoder(Charset.forName("US-ASCII"));


    TelnetClientInitializer() {
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast(TELNET_MESSAGE_DECODER);
        pipeline.addLast(EVENT_DECODER);
        pipeline.addLast(COMMAND_ENCODER);

        // and then business logic.
        pipeline.addLast(new Handshake());
    }
}

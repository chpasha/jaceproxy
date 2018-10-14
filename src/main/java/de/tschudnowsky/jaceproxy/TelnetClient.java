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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Simplistic telnet client.
 */
public final class TelnetClient {

    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "62062"));

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new AceStreamClientInitializer("http://91.92.66.82/trash/ttv-list/acelive/ttv_23136.acelive", null));

            // Start the connection attempt.
            Channel ch = b.connect(HOST, PORT).sync().channel();

			ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

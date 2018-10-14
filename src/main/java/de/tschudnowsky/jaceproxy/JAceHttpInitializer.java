package de.tschudnowsky.jaceproxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 19:02
 */
public class JAceHttpInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(final SocketChannel ch) {
        ch.pipeline()
          .addLast(new LoggingHandler(LogLevel.INFO))
          .addLast("codec", new HttpServerCodec())
          .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
          .addLast("request", new JAceHttpHandler());
    }
}
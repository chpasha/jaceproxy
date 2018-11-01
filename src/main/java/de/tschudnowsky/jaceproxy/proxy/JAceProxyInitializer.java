package de.tschudnowsky.jaceproxy.proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 19:02
 */
public class JAceProxyInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(final SocketChannel ch) {
        ch.pipeline()
          //.addLast(new LoggingHandler(LogLevel.INFO))
          .addLast(new HttpServerCodec())
          .addLast(new HttpObjectAggregator(65536))
          .addLast(new ChunkedWriteHandler())
          .addLast( new HttpHandler())
        ;
    }
}

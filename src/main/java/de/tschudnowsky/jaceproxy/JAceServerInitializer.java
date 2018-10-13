package de.tschudnowsky.jaceproxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class JAceServerInitializer extends ChannelInitializer<SocketChannel> {


    public JAceServerInitializer() {
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.INFO),
                new JAceServerHandler());
    }
}
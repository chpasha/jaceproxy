package de.tschudnowsky.jaceproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 18:18
 */
public final class JAceServer {
    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "9000"));

    public static void main(String[] args) throws Exception {

        // Configure the bootstrap.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new JAceServerInitializer())
             .childOption(ChannelOption.AUTO_READ, false)
             .bind(LOCAL_PORT)
             .sync()
             .channel()
             .closeFuture()
             .sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

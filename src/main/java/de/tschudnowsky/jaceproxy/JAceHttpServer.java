package de.tschudnowsky.jaceproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JAceHttpServer {

    private static final int PORT = 8000;

    private ChannelFuture channel;
    private final EventLoopGroup masterGroup;
    private final EventLoopGroup slaveGroup;

    private JAceHttpServer() {
        masterGroup = new NioEventLoopGroup();
        slaveGroup = new NioEventLoopGroup();
    }

    private void start() {
        log.warn("Starting up on port {}", PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        try {
            final ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(masterGroup, slaveGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new JAceHttpInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            channel = bootstrap.bind(PORT).sync();

        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        }
    }

    private void shutdown() {
        log.warn("Shutting down");
        slaveGroup.shutdownGracefully();
        masterGroup.shutdownGracefully();

        try {
            channel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("", e);
        }
    }

    public static void main(String[] args) {
        new JAceHttpServer().start();
    }
}
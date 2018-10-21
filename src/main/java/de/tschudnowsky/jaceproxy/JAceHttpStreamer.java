package de.tschudnowsky.jaceproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * User: pavel
 * Date: 21.10.18
 * Time: 14:53
 */
@Slf4j
public class JAceHttpStreamer extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof String) {
            streamUrl(ctx, (String) msg);
        } else {
            super.write(ctx, msg, promise);
        }
    }

    private void streamUrl(ChannelHandlerContext ctx, String url) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort();

            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {

                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     pipeline.addLast(new LoggingHandler(LogLevel.TRACE));
                     pipeline.addLast(new HttpClientCodec());
                     pipeline.addLast(new ChunkedWriteHandler());
                     //pipeline.addLast(new Download(ctx.channel()));
                 }
             });

            ChannelFuture f = b.connect(SocketUtils.socketAddress(host, port));
            // Wait until the connection attempt succeeds or fails.
            Channel channel = f.sync().channel();
            // Prepare the HTTP request.
            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
            // send request
            channel.writeAndFlush(request);
            // Wait for the server to close the connection.
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("", e);
            ctx.close();
        } finally {
            group.shutdownGracefully();
        }
    }
}

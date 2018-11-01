package de.tschudnowsky.jaceproxy.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * User: pavel
 * Date: 01.11.18
 * Time: 12:39
 */
@Slf4j
public class AceStreamUrlInterceptor extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof URI) {
            stream(ctx, (URI) msg);
        } else
            super.write(ctx, msg, promise);
    }

    private void stream(ChannelHandlerContext ctx, URI uri) {
        try {
            String host = uri.getHost();
            int port = uri.getPort();

            Channel playerChannel = ctx.channel();
            Bootstrap b = new Bootstrap();
            b.group(playerChannel.eventLoop())
             .channel(ctx.channel().getClass())
             .handler(new ChannelInitializer<SocketChannel>() {

                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     pipeline.addLast(new HttpClientCodec())
                             //todo test
                             .addLast(new ReadTimeoutHandler(30))
                             .addLast(new VideoStreamHandler(playerChannel));
                 }
             })
            //TODO need this?
            //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ??)
            //.option(ChannelOption.SO_TIMEOUT, ??)
            ;
            ChannelFuture streamChannel = b.connect(SocketUtils.socketAddress(host, port));
            //Channel channel = f.sync().channel();
            streamChannel.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
                    streamChannel.channel().writeAndFlush(request);
                } else {
                    streamChannel.channel().close();
                    log.error("Failed to download {}", uri.toString());
                    //TODO stopAceClient(ctx);
                }
            });
        } catch (Exception e) {
            log.error("", e);
            //TODO stopAceClient(ctx);
        }
    }
}

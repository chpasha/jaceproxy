package de.tschudnowsky.jaceproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 19:03
 */
@Slf4j
public class JAceHttpHandler extends ChannelInboundHandlerAdapter {

    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "62062"));

    private Channel outboundChannel;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info(msg.toString());
        if (msg instanceof FullHttpRequest) {

            final FullHttpRequest request = (FullHttpRequest) msg;

            /*TODO
            1) parse request
            2) start telnet with outbound channel or smth.
            3) when we get http link by telnet, read it and stream to client
            4) handle close
            */

            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            response.headers().set(TRANSFER_ENCODING, CHUNKED);
            response.headers().set(HttpHeaderNames.CONNECTION, KEEP_ALIVE);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_OCTET_STREAM);
            response.headers().set(HttpHeaderNames.ACCEPT_RANGES, NONE);
            // Write the initial line and the header.
            ctx.write(response);

            final Channel inboundChannel = ctx.channel();

            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop())
             .channel(ctx.channel().getClass())
             .handler(new AceStreamClientInitializer("http://91.92.66.82/trash/ttv-list/acelive/ttv_23136.acelive", inboundChannel));

            // Start the connection attempt.
            ChannelFuture f = b.connect(HOST, PORT);
            outboundChannel = f.channel();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            });
        } else {

            if (outboundChannel.isActive()) {
                outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        // was able to flush out data, start to read the next chunk
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                });
            }

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        log.error("", cause);
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT/*Unpooled.EMPTY_BUFFER*/).addListener(ChannelFutureListener.CLOSE);
        }
    }
}

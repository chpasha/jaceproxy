package de.tschudnowsky.jaceproxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 19:03
 */
public class JAceHttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {

            final FullHttpRequest request = (FullHttpRequest) msg;

            final String responseMessage = "Hello from Netty!";

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    copiedBuffer(responseMessage.getBytes())
            );

            if (HttpUtil.isKeepAlive(request)) {
                response.headers().set(
                        HttpHeaderNames.CONNECTION,
                        HttpHeaderValues.KEEP_ALIVE
                );
            }
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                    "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    responseMessage.length());

            ctx.writeAndFlush(response);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
    }
}

package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * User: pavel
 * Date: 15.12.18
 * Time: 19:45
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpResponseLogger extends SimpleChannelInboundHandler<HttpResponse> {

    public static final HttpResponseLogger INSTANCE = new HttpResponseLogger();

    public HttpResponseLogger() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpResponse msg) {
        log.debug("VERSION: {}, STATUS: {}", msg.protocolVersion(),  msg.status());
        if (!msg.headers().isEmpty()) {
            for (CharSequence name : msg.headers().names()) {
                for (CharSequence value : msg.headers().getAll(name)) {
                    log.debug("HEADER: {} = {}", name, value);
                }
            }
        }
        //we don't want to consume response, who knows if some handler in pipeline will need it someday
        ctx.fireChannelRead(msg);
    }
}

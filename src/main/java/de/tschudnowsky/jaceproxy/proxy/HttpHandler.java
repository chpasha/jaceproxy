package de.tschudnowsky.jaceproxy.proxy;

import de.tschudnowsky.jaceproxy.JAceConfig;
import de.tschudnowsky.jaceproxy.acestream_api.AceStreamClientInitializer;
import de.tschudnowsky.jaceproxy.acestream_api.commands.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private Channel acestreamChannel;
    private ChannelHandler acestreamHandler;
    private LoadAsyncCommand command;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        command = createLoadCommandFromRequest(request.uri());
        sendHttpResponse(ctx);
        spawnAceStreamConnection(ctx.channel());
    }

    @NotNull
    private LoadAsyncCommand createLoadCommandFromRequest(String url) throws IllegalArgumentException, UnsupportedEncodingException {
         /*
          TODO direct_url data efile_url
        */
        String[] segments = url.split("/");
        if (segments.length < 3) {
            throw new IllegalArgumentException("Too few path variables in url " + url);
        }
        String command = segments[1];
        String param = segments[2];
        switch (command) {
            case "url":
            case "torrent":
                String torrentUrl = URLDecoder.decode(param, "UTF-8");
                return new LoadAsyncTorrentCommand(torrentUrl);
            case "content_id":
            case "pid":
                return new LoadAsyncContentIDCommand(param);
            case "infohash":
                return new LoadAsyncInfohashCommand(param);
        }
        throw new IllegalArgumentException("Unsupported url " + url);
    }

    private void sendHttpResponse(ChannelHandlerContext ctx) {
        //TODO maybe if we ever support torrents with multiple files, we have to return m3u instead after loadasync
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(TRANSFER_ENCODING, CHUNKED);
        response.headers().set(HttpHeaderNames.CONNECTION, KEEP_ALIVE);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_OCTET_STREAM);
        response.headers().set(HttpHeaderNames.ACCEPT_RANGES, BYTES);
        ctx.writeAndFlush(response);
    }

    private void spawnAceStreamConnection(Channel inboundChannel) {
        // Theoretically we don't need to instantiate it multiple times, we are in same session
        // and just want to repeat handshake/load/start stuff on timeout
        if (acestreamHandler == null) {
            acestreamHandler = new AceStreamClientInitializer(command, inboundChannel);
        }

        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
         .channel(inboundChannel.getClass())
         .handler(acestreamHandler);

        ChannelFuture f = b.connect(JAceConfig.INSTANCE.getAceHost(), JAceConfig.INSTANCE.getAcePort());
        log.info("Connection to acestream on {}:{}", JAceConfig.INSTANCE.getAceHost(), JAceConfig.INSTANCE.getAcePort());
        f.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("Could not connect to acestream engine");
                closeOnFlush(inboundChannel);
            }
        });
        acestreamChannel = f.channel();
        acestreamChannel.closeFuture().addListener(future -> closeOnFlush(inboundChannel));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        shutdownAceClient();
    }

    private void shutdownAceClient() {
        if (acestreamChannel != null && acestreamChannel.isActive()) {
            stopAceClient();
            acestreamChannel.writeAndFlush(new ShutdownCommand())
                            .addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        if (isConnectionClosedByClient(cause)) {
            log.warn("Connection closed by client");
        } else {
            log.error("", cause);

            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    copiedBuffer(cause.getMessage().getBytes())
            ));
        }
        shutdownAceClient();
        closeOnFlush(ctx.channel());
    }

    private boolean isConnectionClosedByClient(Throwable cause) {
        return cause instanceof IOException &&
                cause.getMessage().equals("Connection reset by peer");
    }

    private void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void onReadTimeoutWhileStreaming(Channel playerChannel) {
        log.warn("Timeout reading from acestream, restarting broadcast");
        stopAceClient();
        spawnAceStreamConnection(playerChannel);
    }

    private void stopAceClient() {
        if (acestreamChannel != null && acestreamChannel.isActive()) {
            acestreamChannel.writeAndFlush(new StopCommand());
        }
    }

}

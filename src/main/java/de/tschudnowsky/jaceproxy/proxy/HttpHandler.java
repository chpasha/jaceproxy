package de.tschudnowsky.jaceproxy.proxy;

import de.tschudnowsky.jaceproxy.acestream_api.AceStreamClientInitializer;
import de.tschudnowsky.jaceproxy.acestream_api.commands.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 19:03
 */
@Slf4j
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    //private static final String HOST = System.getProperty("host", "192.168.9.20");
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "62062"));

    private Channel acestreamChannel;
    private ChannelHandler acestreamHandler;
    private LoadAsyncCommand command;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        command = createLoadCommandFromRequest(request.uri());
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
                String pid = param;
                return new LoadAsyncContentIDCommand(pid);
            case "infohash":
                String infohash = param;
                return new LoadAsyncInfohashCommand(infohash);
        }
        throw new IllegalArgumentException("Unsupported url " + url);
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

        ChannelFuture f = b.connect(HOST, PORT);
        f.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("Could not connect to acestream engine on {}:{}", HOST, PORT);
                closeOnFlush(inboundChannel);
            }
        });
        acestreamChannel = f.channel();
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

    void onReadTimeoutWhileStreaming(Channel playerChannel) {
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

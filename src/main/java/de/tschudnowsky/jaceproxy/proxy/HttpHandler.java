package de.tschudnowsky.jaceproxy.proxy;

import de.tschudnowsky.jaceproxy.JAceConfig;
import de.tschudnowsky.jaceproxy.acestream_api.AceStreamClientInitializer;
import de.tschudnowsky.jaceproxy.acestream_api.commands.LoadAsyncCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.LoadAsyncContentIDCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.LoadAsyncInfohashCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.LoadAsyncTorrentCommand;
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
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: pavel
 * Date: 13.10.18
 * Time: 19:03
 */
@Slf4j
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

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
        Integer fileIndex = segments.length > 3 ? toInt(segments[3]) : null;
        switch (command) {
            case "url":
            case "torrent":
                String torrentUrl = URLDecoder.decode(param, "UTF-8");
                return new LoadAsyncTorrentCommand(torrentUrl, fileIndex);
            case "content_id":
            case "pid":
                return new LoadAsyncContentIDCommand(param, fileIndex);
            case "infohash":
                return new LoadAsyncInfohashCommand(param, fileIndex);
        }
        throw new IllegalArgumentException("Unsupported url " + url);
    }

    private Integer toInt(String value) {
        try {
            //file index should be 1-based, we deal with normal people, not programmers ;)
            return isNotBlank(value) ? Integer.parseInt(value) - 1 : null;
        } catch (NumberFormatException e) {
            log.error("Error parsing file index", e);
            return null;
        }
    }

    private void spawnAceStreamConnection(Channel inboundChannel) {
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
         .channel(inboundChannel.getClass())
         .handler(new AceStreamClientInitializer(command, inboundChannel));

        ChannelFuture f = b.connect(JAceConfig.INSTANCE.getAceHost(), JAceConfig.INSTANCE.getAcePort());
        log.info("Connection to acestream on {}:{}", JAceConfig.INSTANCE.getAceHost(), JAceConfig.INSTANCE.getAcePort());
        f.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("Could not connect to acestream engine");
                closeOnFlush(inboundChannel);
            }
        });
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
}

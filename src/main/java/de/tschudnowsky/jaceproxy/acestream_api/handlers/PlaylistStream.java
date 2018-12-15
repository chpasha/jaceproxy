package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.SocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;

import java.net.URI;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: pavel
 * Date: 15.12.18
 * Time: 18:29
 */
@Slf4j
@RequiredArgsConstructor
public class PlaylistStream extends SimpleChannelInboundHandler<HttpObject> {

    private StringBuilder response = new StringBuilder();

    private final ChannelGroup clients;
    private final Start startHandler;
    private List<String> urls;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpContent) {
            readHttpContent((HttpContent) msg, ctx);
        }
    }

    private void readHttpContent(HttpContent msg, ChannelHandlerContext ctx) {
        response.append(msg.content().toString(Charsets.UTF_8));
        if (msg instanceof LastHttpContent) {
            log.info("Playlist loaded");
            String playlist = response.toString();
            log.debug(playlist);
            urls = Stream.of(playlist.split("\n"))
                         .filter(item -> item.startsWith("http"))
                         .collect(Collectors.toList());

            if (urls.isEmpty()) {
                log.warn("No http entries found in playlist, exiting");
                ctx.close();
            } else {

                Bootstrap b = new Bootstrap();
                b.group(clients.iterator().next().eventLoop())
                 .channel(ctx.channel().getClass())
                 .handler(new HttpStreamInitializer(new VideoStream(clients, startHandler, false)));

                streamUrl(ctx, b);
            }
        }
    }

    private void streamUrl(ChannelHandlerContext ctx, Bootstrap b) {
        try {
            if (clients.isEmpty()) {
                log.info("Stopping playlist playback");
                ctx.close();
                return;
            }
            ListIterator<String> iterator = urls.listIterator();
            if (!iterator.hasNext()) {
                log.info("Reached end of playlist, exiting");
                ctx.close();
                return;
            }

            String url = iterator.next();
            iterator.remove();
            log.debug("Streaming url {}", url);
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort();
            ChannelFuture streamChannel = b.connect(SocketUtils.socketAddress(host, port));
            streamChannel.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
                    streamChannel.channel().writeAndFlush(request);
                } else {
                    streamChannel.channel().close();
                    ctx.close();
                    log.error("Failed to download {}", uri.toString());
                }
            });
            streamChannel.channel().closeFuture().addListener((ChannelFutureListener) future -> {
                log.info("Finished streaming url {}", url);
                streamUrl(ctx, b);
            });

        } catch (Exception e) {
            log.error("", e);
            ctx.close();
        }
    }
}

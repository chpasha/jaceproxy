/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.JAceConfig;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;


/**
 *
 */
@Sharable
@Slf4j
public class VideoStream extends SimpleChannelInboundHandler<HttpObject> {

    private final ChannelGroup playerChannelGroup;
    private final Start startHandler;

    //we should always notify clients on channel-deactivate unless we got timeout
    //in this case clients should wait for restart
    private boolean shouldCloseClients = true;


    VideoStream(@NotNull ChannelGroup playerChannelGroup, @NotNull Start startHandler) {
        super(false);
        this.startHandler = startHandler;
        // We don't have to track the client channels for closed event since StopOrShutdown does just that
        // When all clients are gone it will issue stop event and we will get LastHttpContent here
        this.playerChannelGroup = playerChannelGroup;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        log.info("Acestream closed connection, stopping download");
        if (shouldCloseClients) {
            log.info("Notifying clients of streaming end");
            playerChannelGroup.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)  {
        if (msg instanceof HttpResponse) {
            logHttpResponse((HttpResponse) msg);
        }
        if (msg instanceof HttpContent) {
            streamHttpContent((HttpContent) msg, ctx);
        }
    }

    private void logHttpResponse(HttpResponse msg) {
        try {
            log.debug("STATUS: {}", msg.status());
            log.debug("VERSION: {}", msg.protocolVersion());
            if (!msg.headers().isEmpty()) {
                for (CharSequence name : msg.headers().names()) {
                    for (CharSequence value : msg.headers().getAll(name)) {
                        log.debug("HEADER: {} = {}", name, value);
                    }
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void streamHttpContent(HttpContent msg, ChannelHandlerContext ctx) {
        if (msg instanceof LastHttpContent) {
            log.warn("Reached end of stream");
            playerChannelGroup.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            shouldCloseClients = false;
            ctx.close();
            msg.release();
        } else {
            playerChannelGroup.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            log.error("Timeout while streaming content");
            if (JAceConfig.INSTANCE.getRestartOnTimeout()) {
                shouldCloseClients = false;
                startHandler.onReadTimeoutWhileStreaming();
            } else {
                shouldCloseClients = true;
            }
        } else {
            log.error("Streaming failed", cause);
            playerChannelGroup.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        ctx.close();
    }
}

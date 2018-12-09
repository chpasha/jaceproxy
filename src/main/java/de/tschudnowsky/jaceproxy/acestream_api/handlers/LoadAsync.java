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
import de.tschudnowsky.jaceproxy.JAceHttpServer;
import de.tschudnowsky.jaceproxy.acestream_api.commands.LoadAsyncCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StartCommand;
import de.tschudnowsky.jaceproxy.acestream_api.commands.StartInfohashCommand;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.LoadAsyncResponseEvent;
import de.tschudnowsky.jaceproxy.acestream_api.events.StatusEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AsciiString;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.util.List;

import static de.tschudnowsky.jaceproxy.acestream_api.events.LoadAsyncResponseEvent.TransportFileContentDescription.ONE_AUDIO_VIDEO;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * >>> LOADASYNC 96367 TORRENT http://91.92.66.82/trash/ttv-list/acelive/ttv_1016_all.acelive 0 0 0
 * <<< STATUS main:loading
 * {"infohash": "45f63d30779b7c8859576b12a05317d16e73404c", "checksum": "8f379924f4c91df00c03e83ccf905891c6aeb981"}
 */
@Sharable
@Slf4j
@RequiredArgsConstructor
public class LoadAsync extends SimpleChannelInboundHandler<Event> {

    @NonNull
    private final LoadAsyncCommand loadAsyncCommand;

    @NonNull
    private final Channel inboundChannel;

    private int requestId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("{}", loadAsyncCommand);
        requestId = loadAsyncCommand.getRequestId();
        ctx.writeAndFlush(loadAsyncCommand).sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event) {
        if (event instanceof StatusEvent) {
            return;
        }
        if (notValidResponse(event)) {
            log.error("Closing because of invalid response");
            ctx.close();
            inboundChannel.close();
            return;
        }
        log.info("{}", event);
        LoadAsyncResponseEvent responseEvent = (LoadAsyncResponseEvent) event;

        if (responseEvent.getResponse().getStatus() == ONE_AUDIO_VIDEO || loadAsyncCommand.getFileIndex() != null) {
            sendHttpResponseForSingleVideo();
            startSingleVideo(ctx, responseEvent);
        } else {
            ctx.close(); //we don't need ace context anymore
            sendHttpResponseForMultipleVideos(responseEvent.getResponse());
        }
    }

    private void sendHttpResponseForSingleVideo() {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(TRANSFER_ENCODING, CHUNKED);
        response.headers().set(HttpHeaderNames.CONNECTION, KEEP_ALIVE);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_OCTET_STREAM);
        inboundChannel.writeAndFlush(response);
    }

    private void startSingleVideo(ChannelHandlerContext ctx, LoadAsyncResponseEvent responseEvent) {
        int fileIndex = defaultIfNull(loadAsyncCommand.getFileIndex(), 0);
        //control bounds
        fileIndex = Math.min(fileIndex, responseEvent.getResponse().getFiles().size() - 1);
        fileIndex = Math.max(0, fileIndex);

        if (isBlank(responseEvent.getResponse().getInfohash())) {
            log.error("I've got LoadAsyncResponse without Infohash: {}", responseEvent.getResponse());
            exceptionCaught(ctx, new IllegalStateException("Infohash missing"));
        }

        String channelGroupName = responseEvent.getResponse().getFiles().get(fileIndex).getFilename();
        ChannelGroup group = JAceHttpServer.getOrCreateChannelGroup(
                responseEvent.getResponse().getInfohash() + "_" + fileIndex,
                channelGroupName);
        log.info("Adding channel {} to group {}", responseEvent.getResponse().getInfohash(), group.name());
        group.add(inboundChannel);
        // if it is not the first channel in group, than broadcast is already running, no need to start anything
        if (group.size() > 1) {
            ctx.close();
            return;
        }

        StartCommand startCommand = createStartCommand(responseEvent.getResponse(), fileIndex);
        log.info("{}", startCommand);
        ctx.pipeline().addLast(new Start(startCommand, group));
        ctx.pipeline().remove(this);
        ctx.fireChannelActive();
    }

    private void sendHttpResponseForMultipleVideos(LoadAsyncResponseEvent.Response loadAsync) {
        String host = ((InetSocketAddress) inboundChannel.localAddress()).getAddress().getHostAddress();
        StringBuilder m3u = new StringBuilder("#EXTM3U\n");
        loadAsync.getFiles()
                 .stream()
                 .sorted()
                 .forEach(file -> {

            m3u.append(String.format("#EXTINF:-1, %s\n", file.getFilename()));
            m3u.append(String.format("http://%s:%s/infohash/%s/%s\n",
                    host, JAceConfig.INSTANCE.getPort(),
                    loadAsync.getInfohash(), file.getIndex() + 1/*Human readable*/));

        });
        String content = m3u.toString();
        int contentLength = ByteBufUtil.utf8Bytes(content);
        ByteBuf buffer = Unpooled.buffer(contentLength);
        ByteBufUtil.writeUtf8(buffer, content);

        DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, AsciiString.cached("application/x-mpegurl"));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        response.headers().set(HttpHeaderNames.CONNECTION, CLOSE);
        inboundChannel.write(response);
        inboundChannel.writeAndFlush(buffer);
    }

    @NotNull
    private StartCommand createStartCommand(LoadAsyncResponseEvent.Response loadAsyncResponse, int fileIndex) {
        LoadAsyncResponseEvent.TransportFile transportFile = loadAsyncResponse.getFiles().get(fileIndex);
        MDC.put("FILENAME", String.format("[%.15s]", transportFile.getFilename()));
        List<Integer> fileIndexes = singletonList(fileIndex);
        // TODO I have a feeling - there is no point in any Start command except for StartInfohash since we always receive infohash
        // as LoadAsyncResponse - test if there are any exceptions
        if (loadAsyncResponse.getInfohash() != null) {
            return new StartInfohashCommand(loadAsyncResponse.getInfohash(), fileIndexes);
        }
        /*switch (loadAsyncCommand.getType()) {
            case TORRENT:
                return new StartTorrentCommand(((LoadAsyncTorrentCommand) loadAsyncCommand).getTorrentUrl(), fileIndexes, transportFile.getStreamId());
            case INFOHASH:
                return new StartInfohashCommand(((LoadAsyncInfohashCommand) loadAsyncCommand).getInfohash(), fileIndexes);
            case RAW:
                return new StartRawCommand(((LoadAsyncRawTransportFileCommand) loadAsyncCommand).getTransportFileAsBase64(), fileIndexes);
            case PID:
                return new StartPidCommand(((LoadAsyncContentIDCommand) loadAsyncCommand).getContentId(), fileIndexes);
        }*/
        throw new IllegalArgumentException(loadAsyncCommand.getClass().getSimpleName() + " is not supported yet");
    }

    private boolean notValidResponse(Event event) {
        if (event instanceof LoadAsyncResponseEvent) {
            return notValidLoadAsyncResponse((LoadAsyncResponseEvent) event);
        } else {
            log.error("Didn't receive LOADRESP");
            return true;
        }
    }

    private boolean notValidLoadAsyncResponse(LoadAsyncResponseEvent event) {
        if (event.getRequestId() != requestId) {
            log.error("Received wrong responseId. Expected {}, but got {}", requestId, event.getRequestId());
            return true;
        }
        if (event.getResponse().getStatus() == LoadAsyncResponseEvent.TransportFileContentDescription.ERROR_RETRIEVING) {
            log.error("AceEngine failed to load transport data");
            return true;
        }
        if (event.getResponse().getStatus() == LoadAsyncResponseEvent.TransportFileContentDescription.NO_AUDI_VIDEO) {
            log.error("Transport file doesn't contain any audio or video");
            return true;
        }
        return false;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("LoadAsync failed", cause);
        ctx.close();
    }
}

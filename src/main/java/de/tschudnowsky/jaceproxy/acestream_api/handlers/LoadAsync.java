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

import de.tschudnowsky.jaceproxy.JAceHttpServer;
import de.tschudnowsky.jaceproxy.acestream_api.commands.*;
import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.LoadAsyncResponseEvent;
import de.tschudnowsky.jaceproxy.acestream_api.events.StatusEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

import java.util.List;

import static de.tschudnowsky.jaceproxy.acestream_api.events.LoadAsyncResponseEvent.TransportFileContentDescription.ONE_AUDIO_VIDEO;
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
            startSingleVideo(ctx, responseEvent);
        } else {
            //TODO send m3u
        }
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
        switch (loadAsyncCommand.getType()) {
            case TORRENT:
                return new StartTorrentCommand(((LoadAsyncTorrentCommand) loadAsyncCommand).getTorrentUrl(), fileIndexes, transportFile.getStreamId());
            case INFOHASH:
                return new StartInfohashCommand(((LoadAsyncInfohashCommand) loadAsyncCommand).getInfohash(), fileIndexes);
            case RAW:
                return new StartRawCommand(((LoadAsyncRawTransportFileCommand) loadAsyncCommand).getTransportFileAsBase64(), fileIndexes);
            case PID:
                return new StartPidCommand(((LoadAsyncContentIDCommand) loadAsyncCommand).getContentId(), fileIndexes);
        }
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

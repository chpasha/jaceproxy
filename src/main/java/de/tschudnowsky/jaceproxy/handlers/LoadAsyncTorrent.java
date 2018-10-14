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
package de.tschudnowsky.jaceproxy.handlers;

import de.tschudnowsky.jaceproxy.api.commands.LoadAsyncTorrentCommand;
import de.tschudnowsky.jaceproxy.api.events.Event;
import de.tschudnowsky.jaceproxy.api.events.LoadAsyncResponseEvent;
import de.tschudnowsky.jaceproxy.api.events.StatusEvent;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * >>> LOADASYNC 96367 TORRENT http://91.92.66.82/trash/ttv-list/acelive/ttv_1016_all.acelive 0 0 0
 * <<< STATUS main:loading
 * {"infohash": "45f63d30779b7c8859576b12a05317d16e73404c", "checksum": "8f379924f4c91df00c03e83ccf905891c6aeb981"}
 */
@Sharable
@Slf4j
@RequiredArgsConstructor
public class LoadAsyncTorrent extends SimpleChannelInboundHandler<Event> {

    private final String url;
    private int requestId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoadAsyncTorrentCommand loadAsync = new LoadAsyncTorrentCommand(url);
        requestId = loadAsync.getRequestId();
        ctx.writeAndFlush(loadAsync)
           .sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event) {
        if (event instanceof StatusEvent) {
            return;
        }
        if (notValidResponse(event)) {
            ctx.close();
        }

        LoadAsyncResponseEvent responseEvent = (LoadAsyncResponseEvent) event;

    }

    private boolean notValidResponse(Event event) {
        if (event instanceof LoadAsyncResponseEvent) {
            return validateLoadAsyncResponse((LoadAsyncResponseEvent) event);
        } else {
            log.error("Didn't receive LOADRESP");
            return false;
        }
    }

    private boolean validateLoadAsyncResponse(LoadAsyncResponseEvent event) {
        if (event.getRequestId() != requestId) {
            log.error("Received wrong responseId. Expected {}, but got {}", requestId, event.getRequestId());
            return false;
        }
        if (event.getResponse().getStatus() == LoadAsyncResponseEvent.TransportFileContentDescription.ERROR_RETRIEVING) {
            log.error("AceEngine failed to load transport data");
            return false;
        }
        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("LoadAsync Failed failed", cause);
        ctx.close();
    }
}

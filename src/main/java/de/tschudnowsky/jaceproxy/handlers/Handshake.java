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

import de.tschudnowsky.jaceproxy.api.commands.HelloCommand;
import de.tschudnowsky.jaceproxy.api.commands.ReadyCommand;
import de.tschudnowsky.jaceproxy.api.events.AuthEvent;
import de.tschudnowsky.jaceproxy.api.events.Event;
import de.tschudnowsky.jaceproxy.api.events.HelloEvent;
import de.tschudnowsky.jaceproxy.api.events.NotReadyEvent;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * >>> HELLOBG version=3
 * <<< HELLOTS version=3.1.16 version_code=3011600 key=191f7f55a9 http_port=6878 bmode=0
 * >>> READY key=n51LvQoTlJzNGaFxseRK-1e65ed7ed29d8147528790327fecd5931420e281
 * <<< AUTH 0/1
 */
@Sharable
@Slf4j
public class Handshake extends SimpleChannelInboundHandler<Event> {

    private static final int PROTOCOL_VERSION = 3;
    private static final String productKey = "n51LvQoTlJzNGaFxseRK-uvnvX-sD4Vm5Axwmc4UcoD-jruxmKsuJaH0eVgE";
    //private static final String productKey = "kjYX790gTytRaXV04IvC-xZH3A18sj5b1Tf3I-J5XVS1xsj-j0797KwxxLpBl26HPvWMm\n";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new HelloCommand(PROTOCOL_VERSION))
           .sync();
        //TODO timeout?
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event event) throws Exception {
        if (event instanceof HelloEvent) {
            sendReadyCommand(ctx, (HelloEvent) event);
        } else if (event instanceof AuthEvent) {
            log.info("Handshake was successful");
            ctx.pipeline().remove(this);
            ctx.fireChannelActive(); //Initiate next Handler in pipeline
        } else if (event instanceof NotReadyEvent) {
            log.error("Handshake failed, acestream not ready. Wrong product key? ");
        }
    }

    private void sendReadyCommand(ChannelHandlerContext ctx, HelloEvent event) throws InterruptedException {
        String responseKey = generateResponseKey(event.getRequestKey());
        ctx.writeAndFlush(new ReadyCommand(responseKey))
           .sync();
    }

    private String generateResponseKey(String requestKey) {
        String signature = DigestUtils.sha1Hex(requestKey + productKey);
        return productKey.split("-")[0] + "-" + signature;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Handshake failed", cause);
        ctx.close();
    }
}

package de.tschudnowsky.jaceproxy.acestream_api.handlers;

import de.tschudnowsky.jaceproxy.JAceConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;

/**
 * User: pavel
 * Date: 15.12.18
 * Time: 18:19
 */
@RequiredArgsConstructor
public class HttpStreamInitializer extends ChannelInitializer<Channel> {

    private final VideoStream streamHandler;

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
          .addLast(new HttpClientCodec())
          .addLast(HttpResponseLogger.INSTANCE)
          .addLast(new ReadTimeoutHandler(JAceConfig.INSTANCE.getStreamTimeout()))
          .addLast(streamHandler);
    }
}

package de.tschudnowsky.jaceproxy.acestream_api;

import de.tschudnowsky.jaceproxy.acestream_api.events.Event;
import de.tschudnowsky.jaceproxy.acestream_api.events.EventMapper;
import de.tschudnowsky.jaceproxy.acestream_api.events.EventMapperFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;

import static de.tschudnowsky.jaceproxy.acestream_api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 14:09
 */
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class EventDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Charset charset;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        String rawValue = msg.toString(charset);
        EventMapper<?> mapper = EventMapperFactory.findMapper(rawValue);
        if (mapper != null) {
            //Strip out event name
            int startOfProperties = rawValue.indexOf(PROPERTY_SEPARATOR);
            if (startOfProperties > -1) {
                rawValue = rawValue.substring(startOfProperties).trim();
            }
            Event event = mapper.readValue(rawValue);
            if (event != null) {
                log.trace("Event received: {}", event);
                out.add(event);
            } else {
                log.error("Mapper {} could not decode event {}", mapper, rawValue);
            }
        } else {
            log.warn("Unknown event received: {}", rawValue);
        }
    }
}

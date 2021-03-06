package de.tschudnowsky.jaceproxy.acestream_api;

import de.tschudnowsky.jaceproxy.acestream_api.commands.Command;
import de.tschudnowsky.jaceproxy.acestream_api.commands.CommandMapper;
import de.tschudnowsky.jaceproxy.acestream_api.commands.CommandMapperFactory;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 16:25
 */
@Slf4j
public class CommandEncoder extends MessageToMessageEncoder<Command> {

    private final Charset charset;

    /**
     * Creates a new instance with the current system character set.
     */
    public CommandEncoder() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates a new instance with the specified character set.
     */
    public CommandEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, List<Object> out)  {
        if (command == null) {
            return;
        }
        CommandMapper<Command> mapper = CommandMapperFactory.getCommandMapper(command);
        if (mapper != null) {
            CharSequence commandAsString = mapper.writeAsString(command);
            log.debug("Sending command: {}", commandAsString);
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(commandAsString + "\r\n"), charset));
        } else {
            log.warn("No mapper found for command {}", command);
        }
    }
}

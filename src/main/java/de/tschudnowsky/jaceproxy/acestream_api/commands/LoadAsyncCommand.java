package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class LoadAsyncCommand extends CommandImpl {

    private static final AtomicInteger requestIdGenerator = new AtomicInteger(Integer.MAX_VALUE);

    private final int requestId = requestIdGenerator.decrementAndGet();
    private final String developerAffiliateZone = "0 0 0";

    private final Type type;

    @Nullable
    private Integer fileIndex;

    public enum Type {
        TORRENT, INFOHASH, RAW, PID
    }

    LoadAsyncCommand(Type type, @Nullable Integer fileIndex) {
        super("LOADASYNC");
        this.type = type;
        this.fileIndex = fileIndex;
    }
}

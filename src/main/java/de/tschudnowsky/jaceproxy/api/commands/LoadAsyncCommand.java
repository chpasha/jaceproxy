package de.tschudnowsky.jaceproxy.api.commands;

import lombok.*;

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
    private String developerAffiliateZone = "0 0 0";

    private final Type type;

    enum Type {
        TORRENT, INFOHASH, RAW, PID
    }

    LoadAsyncCommand(Type type) {
        super("LOADASYNC");
        this.type = type;
    }
}

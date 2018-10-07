package de.tschudnowsky.jaceproxy.api;

import lombok.Data;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:45
 */
@Data
public abstract class MessageImpl implements Message {

    private String name;

    protected MessageImpl() {
    }

    public MessageImpl(String name) {
        this.name = name;
    }
}

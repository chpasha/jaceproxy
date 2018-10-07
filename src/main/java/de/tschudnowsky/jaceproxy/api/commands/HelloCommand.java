package de.tschudnowsky.jaceproxy.api.commands;

import lombok.*;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HelloCommand extends CommandImpl {

    private Integer version;

    public HelloCommand(Integer version) {
        super("HELLOBG");

        this.version = version;
    }
}

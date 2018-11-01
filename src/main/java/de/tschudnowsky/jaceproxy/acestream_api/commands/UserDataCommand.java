package de.tschudnowsky.jaceproxy.acestream_api.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 06.10.18
 * Time: 21:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDataCommand extends CommandImpl {

    public static final String FAKE_DATA = "[{\"gender\": 1}, {\"age\": 5}]";

    private String data = FAKE_DATA;

    public UserDataCommand() {
        super("USERDATA");
    }
}

package de.tschudnowsky.jaceproxy.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: pavel
 * Date: 07.10.18
 * Time: 19:22
 *
 * Пользователь не зарегистрирован:
 * <<AUTH 0
 * Пользователь зарегистрирован:
 * <<AUTH 1
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthEvent extends EventImpl {

    private boolean isRegisteredUser;
}

package com.project.Journey.login.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InputNotFoundException extends AuthenticationException {
    public InputNotFoundException(String m) {
        super(m);
    }
}

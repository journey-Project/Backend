package com.project.Journey.login.security.exception;

import org.springframework.security.core.AuthenticationException;

public class PasswordNotMatchException extends AuthenticationException {
    public PasswordNotMatchException(String m) {
        super(m);
    }
}

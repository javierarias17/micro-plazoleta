package com.pragma.powerup.domain.exception;

import java.util.Collections;

public class ForbiddenException extends FunctionalException {

    private static final String ACCESS_DENIED = "Access Denied";

    public ForbiddenException() {
        super(ACCESS_DENIED, Collections.emptyMap());
    }
}

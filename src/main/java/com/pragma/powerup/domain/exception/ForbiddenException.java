package com.pragma.powerup.domain.exception;

import java.util.Collections;
import java.util.Map;

public class ForbiddenException extends FunctionalException {

    private static final String ACCESS_DENIED = "Access Denied";

    public ForbiddenException() {
        super(ACCESS_DENIED, Collections.emptyMap());
    }

    public ForbiddenException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

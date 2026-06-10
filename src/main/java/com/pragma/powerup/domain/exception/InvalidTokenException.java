package com.pragma.powerup.domain.exception;

import java.util.Collections;

public class InvalidTokenException extends FunctionalException {
    public InvalidTokenException(String message) {
        super(message, Collections.emptyMap());
    }
}

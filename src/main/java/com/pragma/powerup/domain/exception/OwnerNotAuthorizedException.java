package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OwnerNotAuthorizedException extends FunctionalException {
    public OwnerNotAuthorizedException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

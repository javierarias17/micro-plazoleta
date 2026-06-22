package com.pragma.powerup.domain.exception;

import java.util.Map;

public class InvalidSecurityPinException extends FunctionalException {
    public InvalidSecurityPinException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

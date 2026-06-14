package com.pragma.powerup.domain.exception;

import java.util.Map;

public class CustomerHasActiveOrderException extends FunctionalException {
    public CustomerHasActiveOrderException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

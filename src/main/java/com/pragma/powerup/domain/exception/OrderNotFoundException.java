package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OrderNotFoundException extends FunctionalException {
    public OrderNotFoundException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

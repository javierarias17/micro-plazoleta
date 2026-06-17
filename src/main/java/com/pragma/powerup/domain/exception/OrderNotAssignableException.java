package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OrderNotAssignableException extends FunctionalException {
    public OrderNotAssignableException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

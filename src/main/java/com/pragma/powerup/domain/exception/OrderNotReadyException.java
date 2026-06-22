package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OrderNotReadyException extends FunctionalException {
    public OrderNotReadyException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

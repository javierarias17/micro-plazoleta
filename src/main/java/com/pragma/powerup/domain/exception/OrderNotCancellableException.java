package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OrderNotCancellableException extends FunctionalException {
    public OrderNotCancellableException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

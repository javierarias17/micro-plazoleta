package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OrderNotInPreparationException extends FunctionalException {
    public OrderNotInPreparationException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

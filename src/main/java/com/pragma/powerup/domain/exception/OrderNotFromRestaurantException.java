package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OrderNotFromRestaurantException extends FunctionalException {
    public OrderNotFromRestaurantException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

package com.pragma.powerup.domain.exception;

import java.util.Map;

public class DishNotFromRestaurantException extends FunctionalException {
    public DishNotFromRestaurantException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

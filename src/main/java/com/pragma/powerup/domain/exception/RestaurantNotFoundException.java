package com.pragma.powerup.domain.exception;

import java.util.Map;

public class RestaurantNotFoundException extends FunctionalException {
    public RestaurantNotFoundException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

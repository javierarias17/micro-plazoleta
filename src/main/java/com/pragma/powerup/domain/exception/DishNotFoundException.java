package com.pragma.powerup.domain.exception;

import java.util.Map;

public class DishNotFoundException extends FunctionalException {
    public DishNotFoundException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

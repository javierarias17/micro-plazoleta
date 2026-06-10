package com.pragma.powerup.domain.exception;

import java.util.Map;

public class CategoryNotFoundException extends FunctionalException {
    public CategoryNotFoundException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

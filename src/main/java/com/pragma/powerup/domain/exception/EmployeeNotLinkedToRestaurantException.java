package com.pragma.powerup.domain.exception;

import java.util.Map;

public class EmployeeNotLinkedToRestaurantException extends FunctionalException {
    public EmployeeNotLinkedToRestaurantException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

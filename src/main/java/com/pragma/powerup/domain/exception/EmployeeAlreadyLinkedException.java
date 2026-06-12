package com.pragma.powerup.domain.exception;

import java.util.Map;

public class EmployeeAlreadyLinkedException extends FunctionalException {
    public EmployeeAlreadyLinkedException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}

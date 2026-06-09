package com.pragma.powerup.domain.exception;

import java.util.Map;

public class OwnerNotFoundException extends FunctionalException {
    public OwnerNotFoundException(String message, Map<String,String> error) {
        super(message, error);
    }
}

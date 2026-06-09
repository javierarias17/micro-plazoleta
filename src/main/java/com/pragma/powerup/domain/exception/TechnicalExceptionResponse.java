package com.pragma.powerup.domain.exception;

import lombok.Getter;

@Getter
public enum TechnicalExceptionResponse {
    USER_VALIDATION_UNAVAILABLE("User validation service is unavailable");

    private final String message;

    TechnicalExceptionResponse(String message) {
        this.message = message;
    }
}

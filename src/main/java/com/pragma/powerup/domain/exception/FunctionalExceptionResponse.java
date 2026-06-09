package com.pragma.powerup.domain.exception;

import lombok.Getter;

@Getter
public enum FunctionalExceptionResponse {
    BUSINESS_VALIDATION_FAILED("Business validation failed"),
    OWNER_NOT_FOUND("The provided user does not exist or does not have the owner role");

    private final String message;

    FunctionalExceptionResponse(String message) {
        this.message = message;
    }
}

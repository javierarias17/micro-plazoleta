package com.pragma.powerup.domain.exception;

import lombok.Getter;

@Getter
public enum FunctionalExceptionResponse {
    BUSINESS_VALIDATION_FAILED("Business validation failed"),
    OWNER_NOT_FOUND("The provided user does not exist or does not have the owner role"),
    RESTAURANT_NOT_FOUND("The restaurant does not exist"),
    OWNER_NOT_AUTHORIZED("You are not authorized to create dishes for this restaurant"),
    CATEGORY_NOT_FOUND("The category does not exist"),
    DISH_NOT_FOUND("The dish does not exist");

    private final String message;

    FunctionalExceptionResponse(String message) {
        this.message = message;
    }
}

package com.pragma.powerup.domain.exception;

import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;

import java.util.Map;

public class FieldsValidationException extends FunctionalException {
    public FieldsValidationException(Map<String, String> errors) {
        super(FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED, errors);
    }
}

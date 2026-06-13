package com.pragma.powerup.infrastructure.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pragma.powerup.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

    private static final String AN_UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred. Please contact the administrator.";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String INVALID_REQUEST_BODY = "Invalid request body";
    private static final String INVALID_REQUEST_PARAMETER = "Invalid request parameter";
    private static final String ERRORS = "errors";
    private static final String FIELD = "field";
    private static final String MESSAGE = "message";

    // region Pre-controller — thrown before the request reaches the controller

    /**
     * Thrown by Jackson when deserializing the {@code @RequestBody}.
     * <p>e.g. malformed JSON, or a field value that cannot be converted to the expected type.</p>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && !invalidFormatException.getPath().isEmpty()) {

            String fieldName = invalidFormatException.getPath().get(0).getFieldName();
            String expectedType = invalidFormatException.getTargetType().getSimpleName();

            Map<String, String> detail = new LinkedHashMap<>();
            detail.put(FIELD, fieldName);
            detail.put(MESSAGE, "Invalid value for field, expected type " + expectedType);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put(MESSAGE, INVALID_REQUEST_BODY);
            response.put(ERRORS, Collections.singletonList(detail));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, INVALID_REQUEST_BODY);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Thrown by Spring MVC during parameter binding when a {@code @RequestParam},
     * {@code @PathVariable} or {@code @RequestHeader} value cannot be converted
     * to the declared Java type (int, Long, UUID, enum, etc.).
     * <p>e.g. {@code GET /restaurant?page=abc} where {@code page} is declared as {@code int}.</p>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";

        Map<String, String> detail = new LinkedHashMap<>();
        detail.put(FIELD, ex.getName());
        detail.put(MESSAGE, "Invalid value '" + ex.getValue() + "', expected type " + expectedType);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, INVALID_REQUEST_PARAMETER);
        response.put(ERRORS, Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // endregion

    // region Controller — thrown during controller-level validation

    /**
     * Thrown when the controller triggers Bean Validation via {@code @Valid} on a {@code @RequestBody}
     * and one or more fields in the DTO fail their constraints
     * ({@code @NotBlank}, {@code @Size}, {@code @Pattern}, etc.).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorDetail = new HashMap<>();
                    errorDetail.put(FIELD, error.getField());
                    errorDetail.put(MESSAGE, error.getDefaultMessage());
                    return errorDetail;
                })
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, VALIDATION_FAILED);
        response.put(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // endregion

    // region Domain — thrown by business/domain use cases

    @ExceptionHandler(FieldsValidationException.class)
    public ResponseEntity<Map<String, Object>> handleFieldsValidationException(
            FieldsValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOwnerNotFoundException(OwnerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantNotFoundException(RestaurantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(DishNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDishNotFoundException(DishNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OwnerNotAuthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleOwnerNotAuthorizedException(OwnerNotAuthorizedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(EmployeeAlreadyLinkedException.class)
    public ResponseEntity<Map<String, Object>> handleEmployeeAlreadyLinkedException(EmployeeAlreadyLinkedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    // endregion

    // region Infrastructure — technical and unexpected errors

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<Map<String, Object>> handleTechnicalException(
            TechnicalException ex) {

        log.error("Technical error: {}", ex.getMessage(), ex);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, AN_UNEXPECTED_ERROR_OCCURRED);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(
            Exception ex) {

        log.error("Unexpected error", ex);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, AN_UNEXPECTED_ERROR_OCCURRED);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    // endregion

    // region Helpers

    private Map<String, Object> buildErrorResponse(FunctionalException ex) {
        List<Map<String, String>> errorList = ex.getErrors().entrySet().stream()
                .map(entry -> {
                    Map<String, String> detail = new LinkedHashMap<>();
                    detail.put(FIELD, entry.getKey());
                    detail.put(MESSAGE, entry.getValue());
                    return detail;
                }).toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, ex.getMessage());
        if (!errorList.isEmpty()) {
            response.put(ERRORS, errorList);
        }
        return response;
    }

    // endregion
}

package com.pragma.powerup.infrastructure.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pragma.powerup.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    private static final String INVALID_FIELD_VALUE_TYPE = "Invalid value for field, expected type %s";
    private static final String REQUIRED_PARAM_MISSING = "Required parameter '%s' is missing";
    private static final String INVALID_PARAM_VALUE_TYPE = "Invalid value '%s', expected type %s";
    private static final String METHOD_NOT_ALLOWED_MSG = "HTTP method not allowed for this endpoint";
    private static final String NONE = "none";
    private static final String LOG_TECHNICAL_ERROR = "Technical error: {}";
    private static final String LOG_UNEXPECTED_ERROR = "Unexpected error";

    // region Pre-controller — thrown before the request reaches the controller

    /**
     * Thrown by Jackson when deserializing the {@code @RequestBody}.
     * <p>
     * e.g. malformed JSON, or a field value that cannot be converted to the
     * expected type.
     * </p>
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
            detail.put(MESSAGE, String.format(INVALID_FIELD_VALUE_TYPE, expectedType));

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
     * Thrown by Spring MVC when a required {@code @RequestParam} is missing from
     * the request.
     * <p>
     * e.g. {@code GET /order} without providing the {@code status} parameter.
     * </p>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {

        Map<String, String> detail = new LinkedHashMap<>();
        detail.put(FIELD, ex.getParameterName());
        detail.put(MESSAGE, String.format(REQUIRED_PARAM_MISSING, ex.getParameterName()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, INVALID_REQUEST_PARAMETER);
        response.put(ERRORS, Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Thrown by Spring MVC during parameter binding when a {@code @RequestParam},
     * {@code @PathVariable} or {@code @RequestHeader} value cannot be converted
     * to the declared Java type (int, Long, UUID, enum, etc.).
     * <p>
     * e.g. {@code GET /restaurant?page=abc} where {@code page} is declared as
     * {@code int}.
     * </p>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : NONE;

        Map<String, String> detail = new LinkedHashMap<>();
        detail.put(FIELD, ex.getName());
        detail.put(MESSAGE, String.format(INVALID_PARAM_VALUE_TYPE, ex.getValue(), expectedType));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, INVALID_REQUEST_PARAMETER);
        response.put(ERRORS, Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // endregion

    // region Controller — thrown during controller-level validation

    /**
     * Thrown when the controller triggers Bean Validation via {@code @Valid} on a
     * {@code @RequestBody}
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

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(CustomerHasActiveOrderException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerHasActiveOrderException(
            CustomerHasActiveOrderException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(DishNotFromRestaurantException.class)
    public ResponseEntity<Map<String, Object>> handleDishNotFromRestaurantException(DishNotFromRestaurantException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(EmployeeNotLinkedToRestaurantException.class)
    public ResponseEntity<Map<String, Object>> handleEmployeeNotLinkedToRestaurantException(
            EmployeeNotLinkedToRestaurantException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFoundException(
            OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OrderNotAssignableException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotAssignableException(
            OrderNotAssignableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OrderNotInPreparationException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotInPreparationException(
            OrderNotInPreparationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OrderNotReadyException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotReadyException(
            OrderNotReadyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(InvalidSecurityPinException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSecurityPinException(
            InvalidSecurityPinException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    @ExceptionHandler(OrderNotCancellableException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotCancellableException(
            OrderNotCancellableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(ex));
    }

    // endregion

    // region Infrastructure — technical and unexpected errors

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, METHOD_NOT_ALLOWED_MSG);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<Map<String, Object>> handleTechnicalException(
            TechnicalException ex) {

        log.error(LOG_TECHNICAL_ERROR, ex.getMessage(), ex);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(MESSAGE, AN_UNEXPECTED_ERROR_OCCURRED);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(
            Exception ex) {

        log.error(LOG_UNEXPECTED_ERROR, ex);

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

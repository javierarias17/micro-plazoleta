package com.pragma.powerup.domain.exception.constant;

public class FunctionalMessageConstants {

    private FunctionalMessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BUSINESS_VALIDATION_FAILED = "Business validation failed";
    public static final String OWNER_NOT_FOUND = "The provided user does not exist or does not have the owner role";
    public static final String RESTAURANT_NOT_FOUND = "The restaurant does not exist";
    public static final String CATEGORY_NOT_FOUND = "The category does not exist";
    public static final String DISH_NOT_FOUND = "The dish does not exist";
    public static final String PAGE_MUST_BE_ZERO_OR_POSITIVE = "Page number must be zero or positive";
    public static final String PAGE_SIZE_MUST_BE_POSITIVE = "Page size must be a positive number";
    public static final String CUSTOMER_HAS_ACTIVE_ORDER = "You already have an order in process. You cannot place a new order until it is completed or cancelled";
    public static final String DISH_NOT_AVAILABLE = "One or more dishes are not available or do not exist";
    public static final String EMPLOYEE_NOT_LINKED_TO_RESTAURANT = "The employee is not linked to any restaurant";
    public static final String ORDER_NOT_FOUND = "The order does not exist";
    public static final String ORDER_NOT_IN_ASSIGNABLE_STATUS = "The order cannot be assigned because it is not in PENDING status";
    public static final String ORDER_NOT_IN_PREPARATION = "The order cannot be marked as ready because it is not in IN_PREPARATION status";
    public static final String ORDER_NOT_READY = "The order cannot be delivered because it is not in READY status";
    public static final String INVALID_SECURITY_PIN = "The security PIN is incorrect";
    public static final String ORDER_ALREADY_CANCELLED = "The order is already cancelled";
    public static final String ORDER_NOT_CANCELLABLE = "We are sorry, your order is already being prepared and cannot be cancelled";
    public static final String ORDER_ASSIGNED_TO_ANOTHER_EMPLOYEE = "The order is assigned to another employee";
}

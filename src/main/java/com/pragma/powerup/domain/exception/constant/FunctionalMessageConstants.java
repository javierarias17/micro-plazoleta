package com.pragma.powerup.domain.exception.constant;

public class FunctionalMessageConstants {

    private FunctionalMessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BUSINESS_VALIDATION_FAILED = "Business validation failed";
    public static final String OWNER_NOT_FOUND = "The provided user does not exist or does not have the owner role";
    public static final String RESTAURANT_NOT_FOUND = "The restaurant does not exist";
    public static final String OWNER_NOT_AUTHORIZED_TO_CREATE_DISH = "You are not authorized to create dishes for this restaurant";
    public static final String OWNER_NOT_AUTHORIZED_TO_UPDATE_DISH = "You are not authorized to modify dishes for this restaurant";
    public static final String OWNER_NOT_AUTHORIZED_TO_LINK_EMPLOYEE = "You are not authorized to link employees to this restaurant";
    public static final String CATEGORY_NOT_FOUND = "The category does not exist";
    public static final String DISH_NOT_FOUND = "The dish does not exist";
    public static final String EMPLOYEE_ALREADY_LINKED = "This employee is already linked to a restaurant";
    public static final String PAGE_MUST_BE_ZERO_OR_POSITIVE = "Page number must be zero or positive";
    public static final String PAGE_SIZE_MUST_BE_POSITIVE = "Page size must be a positive number";
    public static final String CUSTOMER_HAS_ACTIVE_ORDER = "You already have an order in process. You cannot place a new order until it is completed or cancelled";
    public static final String DISH_NOT_AVAILABLE = "One or more dishes are not available or do not exist";
    public static final String EMPLOYEE_NOT_LINKED_TO_RESTAURANT = "The employee is not linked to any restaurant";
    public static final String OWNER_NOT_AUTHORIZED_TO_TOGGLE_DISH_STATUS = "You are not authorized to change the status of dishes for this restaurant";
}

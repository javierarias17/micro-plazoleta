package com.pragma.powerup.domain.common;

public class ValidationMessageConstants {

    private ValidationMessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    // Name
    public static final String MSG_NAME_REQUIRED = "Name is required";
    public static final String MSG_NAME_NOT_NUMBERS_ONLY = "Name cannot consist of numbers only";

    // NIT
    public static final String MSG_NIT_REQUIRED = "NIT is required";
    public static final String MSG_NIT_DIGITS_ONLY = "NIT must contain only digits";

    // Address
    public static final String MSG_ADDRESS_REQUIRED = "Address is required";

    // Phone
    public static final String MSG_PHONE_REQUIRED = "Phone is required";
    public static final String MSG_PHONE_FORMAT = "Phone must contain at most 13 characters and must start with + if provided";

    // Logo
    public static final String MSG_LOGO_URL_REQUIRED = "Logo URL is required";

    // IDs
    public static final String MSG_OWNER_ID_REQUIRED = "Owner ID is required";
    public static final String MSG_EMPLOYEE_ID_REQUIRED = "Employee ID is required";
    public static final String MSG_CATEGORY_ID_REQUIRED = "Category ID is required";
    public static final String MSG_RESTAURANT_ID_REQUIRED = "Restaurant ID is required";

    // Dish
    public static final String MSG_DESCRIPTION_REQUIRED = "Description is required";
    public static final String MSG_PRICE_REQUIRED = "Price is required";
    public static final String MSG_PRICE_POSITIVE = "Price must be a positive number";
    public static final String MSG_IMAGE_URL_REQUIRED = "Image URL is required";
}

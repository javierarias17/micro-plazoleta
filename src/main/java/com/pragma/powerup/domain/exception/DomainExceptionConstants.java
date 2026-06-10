package com.pragma.powerup.domain.exception;

public class DomainExceptionConstants {

    private DomainExceptionConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String OWNER_ID = "ownerId";
    public static final String RESTAURANT_ID = "restaurantId";
    public static final String CATEGORY_ID = "categoryId";
}

package com.pragma.powerup.domain.exception.constant;

public class TechnicalMessageConstants {

    private TechnicalMessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String USER_UNAVAILABLE = "User service is unavailable";
    public static final String MESSAGING_UNAVAILABLE = "Messaging service is unavailable";
}

package com.pragma.powerup.domain.common;

public class RegexConstants {

    private RegexConstants() {
        throw new IllegalStateException("Utility class");
    }

    /** Any string that is NOT composed entirely of digits. At least one character required. */
    public static final String RESTAURANT_NAME_REGEX = "^(?!\\d+$).+$";

    /** Only digits (0-9), one or more characters. */
    public static final String NIT_REGEX = "\\d+";

    /** Optional leading +, followed by 1-12 digits; or 1-13 digits with no prefix. Max 13 chars total. */
    public static final String PHONE_REGEX = "^(\\+\\d{1,12}|\\d{1,13})$";
}

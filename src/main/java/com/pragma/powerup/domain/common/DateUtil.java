package com.pragma.powerup.domain.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {

    private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");

    private DateUtil() {}

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(COLOMBIA_ZONE);
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now(COLOMBIA_ZONE);
    }
}

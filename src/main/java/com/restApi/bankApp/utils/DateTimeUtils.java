package com.restApi.bankApp.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateTimeUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Format LocalDateTime to string using default formatter (yyyy-MM-dd HH:mm:ss)
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    /**
     * Format LocalDateTime to string using a custom formatter
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null || formatter == null) {
            return null;
        }
        return dateTime.format(formatter);
    }
    
    /**
     * Convert LocalDateTime to UTC
     */
    public static LocalDateTime toUtc(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcZoned.toLocalDateTime();
    }
    
    /**
     * Get current timestamp in UTC
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
} 
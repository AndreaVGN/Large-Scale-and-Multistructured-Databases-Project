package com.example.WanderHub.demo.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {
    // Formatter per il formato "yyyy-MM-dd"
    private static final DateTimeFormatter DASH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Formatter per il formato "yyyyMMdd"
    private static final DateTimeFormatter NO_DASH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Converte una LocalDate in una stringa con formato "yyyy-MM-dd".
     */
    public static String formatWithDashes(LocalDate date) {
        if (date == null) return null;
        return date.format(DASH_FORMATTER);
    }

    /**
     * Converte una LocalDate in una stringa con formato "yyyyMMdd".
     */
    public static String formatWithoutDashes(LocalDate date) {
        if (date == null) return null;
        return date.format(NO_DASH_FORMATTER);
    }

    /**
     * Converte una stringa "yyyy-MM-dd" in LocalDate.
     */
    public static LocalDate parseWithDashes(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DASH_FORMATTER);
    }

    /**
     * Converte una stringa "yyyyMMdd" in LocalDate.
     */
    public static LocalDate parseWithoutDashes(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, NO_DASH_FORMATTER);
    }
}


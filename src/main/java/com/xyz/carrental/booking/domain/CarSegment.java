package com.xyz.carrental.booking.domain;

/**
 * Represents available car size categories in the car rental system.
 */
public enum CarSegment {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRALARGE; // mapped from ExtraLarge in assignment; use EXTRALARGE for clear enum naming

    /**
     * Converts a string value to the corresponding {@link CarSegment} enum.
     * <p>
     * Accepts case-insensitive and variant spellings such as "EXTRA_LARGE" or "extra large".
     *
     * @param s the input string representing a car segment
     * @return the matching {@link CarSegment}
     * @throws IllegalArgumentException if the input does not match any segment
     */
    public static CarSegment from(String s) {
        return switch (s == null ? "" : s.strip().toUpperCase()) {
            case "SMALL" -> SMALL;
            case "MEDIUM" -> MEDIUM;
            case "LARGE" -> LARGE;
            case "EXTRALARGE", "EXTRA_LARGE", "EXTRA-LARGE", "EXTRA LARGE", "EXTRA" -> EXTRALARGE;
            default -> throw new IllegalArgumentException("Unknown car segment: " + s);
        };
    }
}

package com.makrozai.eligiusconnector.util;

public final class NumberUtils {

    private NumberUtils() {}

    public static double parseDouble(Object value) {
        if (value instanceof Number num) return num.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

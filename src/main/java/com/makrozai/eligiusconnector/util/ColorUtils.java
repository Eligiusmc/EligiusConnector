package com.makrozai.eligiusconnector.util;

import java.awt.Color;

public final class ColorUtils {

    private ColorUtils() {}

    public static Color parseColor(String colorStr, int defaultColor) {
        if (colorStr == null || colorStr.isEmpty()) return new Color(defaultColor);
        try {
            if (colorStr.startsWith("0x") || colorStr.startsWith("0X")) {
                return new Color(Integer.parseInt(colorStr.substring(2), 16));
            } else if (colorStr.startsWith("#")) {
                return new Color(Integer.parseInt(colorStr.substring(1), 16));
            } else {
                return new Color(Integer.parseInt(colorStr));
            }
        } catch (NumberFormatException e) {
            return new Color(defaultColor);
        }
    }
}

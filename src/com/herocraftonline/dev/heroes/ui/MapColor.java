package com.herocraftonline.dev.heroes.ui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public enum MapColor {

    ICE(112, 112, 180, 20),
    DARK_GRAY(79, 79, 79, 44),
    DARK_RED(180, 0, 0, 16),
    RED(220, 0, 0, 19),
    BLUE(55, 55, 220, 51),
    DARK_BLUE(45, 45, 180, 48),
    GREEN(0, 124, 0, 30),
    DARK_GREEN(0, 87, 0, 28);

    private final int red;
    private final int green;
    private final int blue;
    private final int code;
    private final static Map<Integer, MapColor> colors = new HashMap<Integer, MapColor>();

    private MapColor(final int red, final int green, final int blue, final int code) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.code = code;
    }

    /**
     * Gets the data value associated with this color
     * 
     * @return An integer value of this color code
     */
    public int getCode() {
        return code;
    }

    public int getRed() {
        return this.red;
    }

    public int getGreen() {
        return this.green;
    }

    public int getBlue() {
        return this.blue;
    }

    @Override
    public String toString() {
        return "\u00A7" + getCode() + ";";
    }

    /**
     * Gets the color represented by the specified color code
     * 
     * @param code Code to check
     * @return Associative Color with the given code, or null if it doesn't exist
     */
    public static MapColor getByCode(final int code) {
        return colors.get(code);
    }

    public static MapColor getByRGB(final int red, final int green, final int blue) {
        for (MapColor color : MapColor.values()) {
            if (red == color.getRed() && green == color.getGreen() && blue == color.getBlue()) {
                return color;
            }
        }
        return null;
    }

    public static MapColor getByColor(final Color c) {
        for (MapColor color : MapColor.values()) {
            if (c.getRed() == color.getRed() && c.getRed() == color.getGreen() && c.getBlue() == color.getBlue()) {
                return color;
            }
        }
        return null;
    }

    public static Color getColor(final int code) {
        MapColor mColor = getByCode(code);
        if (mColor == null) {
            System.out.print("Uh oh");
            return Color.BLUE;
        }
        return new Color(mColor.getRed(), mColor.getGreen(), mColor.getBlue());
    }

    /**
     * Strips the given message of all color codes
     * 
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return input.replaceAll("(?i)\u00A7[0-F]", "");
    }

    static {
        for (MapColor color : MapColor.values()) {
            // System.out.print("Code - " + color.getCode() + " - " + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
            colors.put(color.getCode(), color);
        }
    }
}

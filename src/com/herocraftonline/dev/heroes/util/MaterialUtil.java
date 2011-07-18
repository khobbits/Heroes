package com.herocraftonline.dev.heroes.util;

import org.bukkit.Material;

public class MaterialUtil {

    public static String capitalize(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String getFriendlyName(Material material) {
        return getFriendlyName(material.toString());
    }

    public static String getFriendlyName(String string) {
        return capitalize(string.toLowerCase().replaceAll("_", " "));
    }
}

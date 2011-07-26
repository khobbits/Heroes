package com.herocraftonline.dev.heroes.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

/**
 * Utility class for converting between colors and map palette indexes.
 */
public final class ColorMap {

    /**
     * This is an indexed array of the RGB values of each notchcolor - encoded in a String.
     * The actual RGB values are retrieved using String.split();
     */
    private static final String[] colors = {
        // ID 0-3
        "", "", "", "",
        // ID 4-7
        "89,125,39", "109,153,48", "27,178,56", "109,153,48",
        // ID 8-11
        "174,164,115", "213,201,140", "247,233,163", "213,201,140",
        // ID 12-15
        "117,117,117", "144,144,144", "167,167,167", "144,144,144",
        // ID 16-19
        "180,0,0", "220,0,0", "255,0,0", "220,0,0",
        // ID 20-23
        "112,112,180", "138,138,220", "160,160,255", "138,138,220",
        // ID 24-27
        "117,117,117", "144,144,144", "167,167,167", "144,144,144",
        // ID 28-31
        "0,87,0", "0,106,0", "0,124,0", "0,106,0",
        // ID 32-35
        "180,180,180", "220,220,220", "255,255,255", "220,220,220",
        // ID 36-39
        "115,118,129", "141,144,158", "164,168,184", "141,144,158",
        // ID 40-43
        "129,74,33", "157,91,40", "183,106,47", "157,91,40",
        // ID 44-47
        "79,79,79", "96,96,96", "112,112,112", "96,96,96",
        // ID 48-51
        "45,45,180", "55,55,220", "64,64,255", "55,55,220",
        // ID 52-55
        "73,58,35", "89,71,43", "104,83,50", "89,71,43", };

    /**
     * Resize an image to fit on a map.
     * @param image The image to resize.
     * @return The resized image.
     */
    public static BufferedImage resizeImage(Image image) {
        BufferedImage result = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, 128, 128, null);
        graphics.dispose();
        return result;
    }

    /**
     * Convert an Image to a byte[] using the palette.
     * @param originalImage The image to convert.
     * @return A byte[128 * 128] containing the pixels of the image.
     */
    public static byte[] imageToBytes(Image originalImage) {
        BufferedImage image = resizeImage(originalImage);

        int[] pixels = new int[128 * 128];

        try {
            PixelGrabber grabber = new PixelGrabber(image, 0, 0, image.getWidth(),image.getHeight(), pixels, 0, image.getWidth());
            grabber.grabPixels(0);
        }
        catch (InterruptedException ex) {
            System.err.println("Error grabbing image: " + ex.getMessage());
        }

        byte[] result = new byte[128 * 128];
        for (int i = 0; i < pixels.length; i++) {
            if (i == pixels.length / 2) {
                //System.out.println("50% converted");
            }
            result[i] = indexOf(new Color(pixels[i]));
        }

        return result;
    }

    /**
     * Convert a byte[] to an Image using the palette.
     * @param bytes The byte[128 * 128] to convert.
     * @return A BufferedImage containing the pixels from bytes.
     */
    public static BufferedImage bytesToImage(byte[] bytes) {
        int[] pixels = new int[128 * 128];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = get(bytes[i]).getRGB();
        }

        BufferedImage result = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, 128, 128, pixels, 0, 128);
        return result;
    }

    /**
     * Get the index of the closest matching color in the palette to the given color.
     * @param r The red component of the color.
     * @param b The blue component of the color.
     * @param g The green component of the color.
     * @return The index in the palette.
     */
    public static byte indexOf(int r, int g, int b) {
        int index = 0;
        double best = -1;
        float[] hsbTarget = Color.RGBtoHSB(r, g, b, null);

        for (int i = 4; i < colors.length; i++) {
            String[] colorString = colors[i].split(",");
            int palR = Integer.parseInt(colorString[0]);
            int palG = Integer.parseInt(colorString[1]);
            int palB = Integer.parseInt(colorString[2]);

            float[] hsbNotch = Color.RGBtoHSB(palR, palG, palB, null);

            double hDiff = Math.abs(hsbTarget[0] - hsbNotch[0]);
            double sDiff = Math.abs(hsbTarget[1] - hsbNotch[1]);
            double bDiff = Math.abs(hsbTarget[2] - hsbNotch[2]);
            double total = hDiff + bDiff + sDiff;

            if (total < best || best == -1) {
                best = total;
                index = i;
            }
        }

        return (byte) index;
    }

    /**
     * Get the index of the closest matching color in the palette to the given color.
     * @param color The Color to match.
     * @return The index in the palette.
     */
    public static byte indexOf(Color color) {
        if (color.getAlpha() < 128) return 0;
        return indexOf(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Get the value of the given color in the palette.
     * @param index The index in the palette.
     * @return The Color of the palette entry.
     */
    public static Color get(byte index) {
        if (index < 4) {
            return new Color(0, 0, 0, 0);
        } else if (index >= colors.length) {
            throw new IndexOutOfBoundsException();
        }
        String[] colorString = colors[index].split(",");
        int r = Integer.parseInt(colorString[0]);
        int g = Integer.parseInt(colorString[1]);
        int b = Integer.parseInt(colorString[2]);
        return new Color(r, g, b);
    }

}

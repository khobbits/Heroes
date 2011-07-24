package com.herocraftonline.dev.heroes.ui;

import com.herocraftonline.dev.heroes.Heroes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * Simple class to render bitmap fonts to a map.
 */
public final class TextRenderer {

    private final HashMap<Character, CharacterSprite> chars = new HashMap<Character, CharacterSprite>();

    /**
     * Initialize the TextRenderer using font.png from the current working directory.
     * It should be a copy of font/default.png from the Minecraft client jar.
     */
    public TextRenderer(Heroes plugin) {
        try {
            BufferedImage image = ImageIO.read(new File(plugin.getDataFolder(), "font.png"));
            //BufferedImage image = ImageIO.read(new File("font.png"));

            for (int row = 0; row < 16; ++row) {
                for (int col = 0; col < 16; ++col) {
                    boolean[] data = new boolean[8 * 8];
                    for (int x = 0; x < 8; ++x) {
                        for (int y = 0; y < 8; ++y) {
                            Color color = new Color(image.getRGB(col * 8 + x, row * 8 + y));
                            data[y * 8 + x] = color.getRed() >= 128;
                        }
                    }
                    setChar((char) (row * 16 + col), new CharacterSprite(data));
                }
            }
        } catch (IOException ex) {
            System.err.println("Unable to read font.png: " + ex.getMessage());
            return;
        }
    }

    /**
     * Set the sprite for a given character.
     *
     * @param ch     The character to set the sprite for.
     * @param sprite The CharacterSprite to set.
     */
    public void setChar(char ch, CharacterSprite sprite) {
        chars.put(ch, sprite);
    }

    /**
     * Render text to a map in the given color.
     *
     * @param map   The MapInfo to render to.
     * @param row   The row to start rendering on.
     * @param col   The column to start rendering on.
     * @param color The palette index of the color to render in.
     * @param text  The text to render.
     */
    public void render(MapInfo map, int row, int col, byte color, String text) {
        validate(text, false);
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == ' ') {
                col += 4;
                continue;
            }

            CharacterSprite sprite = chars.get(text.charAt(i));
            for (int r = 0; r < 8; ++r) {
                for (int c = 0; c < sprite.getWidth(); ++c) {
                    if (sprite.get(r, c)) {
                        map.setData(row + r, col + c, color);
                    }
                }
            }
            col += sprite.getWidth() + 1;
        }
    }

    /**
     * Render text to a map using fancy formatting. Newline (\n) characters
     * will move down one line and return to the original column, and the text
     * color can be changed using sequences such as "�12;", replacing 12 with
     * the palette index of the color (see ColorMap.indexOf).
     *
     * @param map  The MapInfo to render to.
     * @param row  The row to start rendering on.
     * @param col  The column to start rendering on.
     * @param text The formatted text to render.
     */
    public void fancyRender(MapInfo map, int row, int col, String text) {
        int colStart = col;
        byte color = ColorMap.indexOf(0, 0, 0);
        validate(text, true);
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == ' ') {
                col += 4;
                continue;
            } else if (ch == '\n') {
                col = colStart;
                row += 9;
                continue;
            } else if (ch == '\u00A7') {
                int j = text.indexOf(';', i);
                if (j >= 0) {
                    try {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    } catch (NumberFormatException ex) {
                    }
                }
            }

            CharacterSprite sprite = chars.get(text.charAt(i));
            for (int r = 0; r < 8; ++r) {
                for (int c = 0; c < 8; ++c) {
                    if (sprite.get(r, c)) {
                        map.setData(row + r, col + c, color);
                    }
                }
            }
            col += sprite.getWidth() + 1;
        }
    }

    /**
     * Calculate the width of given text.
     *
     * @param text The text.
     * @return The width of the text.
     */
    public int widthOf(String text) {
        validate(text, false);
        int result = 0;
        for (int i = 0; i < text.length(); ++i) {
            result += chars.get(text.charAt(i)).getWidth();
        }
        return result;
    }

    private void validate(String text, boolean fancy) {
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (fancy && (ch == '\u00A7' || ch == '\n')) continue;
            if (chars.get(ch) == null) {
                throw new IllegalArgumentException("Text contains invalid character '" + text.charAt(i) + "'");
            }
        }
    }

    /**
     * The graphics for a single 8x8 character of a bitmap font.
     */
    public static final class CharacterSprite {

        private boolean[] data;
        private int width;

        /**
         * Initialize the character sprite to the given data. The data must be
         * 8 by 8 pixels, with true representing solid and false transparent.
         * Width is automatically calculated based on solid pixels.
         *
         * @param data A boolean[8*8] representing the bitmap.
         */
        public CharacterSprite(boolean[] data) {
            if (data.length != 8 * 8) {
                throw new IllegalArgumentException();
            }
            this.data = data;

            width = 8;
            for (int col = 7; col >= 0; --col) {
                boolean clear = true;
                for (int row = 0; row < 8; ++row) {
                    if (get(row, col)) {
                        clear = false;
                        break;
                    }
                }
                if (clear) {
                    width = col;
                }
            }
        }

        /**
         * Initialize the character sprite to the given data. The data must be
         * 8 by 8 pixels, with true representing solid and false transparent.
         *
         * @param data A boolean[8*8] representing the bitmap.
         */
        public CharacterSprite(boolean[] data, int width) {
            if (data.length != 8 * 8) {
                throw new IllegalArgumentException();
            }
            this.data = data;
            this.width = width;
        }

        /**
         * Get the value of a pixel of the character.
         *
         * @param row The row, in the range [0,8).
         * @param col The column, in the range [0,8).
         * @return True if the pixel is solid, false if transparent.
         */
        public boolean get(int row, int col) {
            if (row < 0 || col < 0 || row >= 8 || col >= 8) return false;
            return data[row * 8 + col];
        }

        /**
         * Get the width of the character sprite.
         *
         * @return The width of the character.
         */
        public int getWidth() {
            return width;
        }

        /**
         * Generate a CharacterSprite from an array of 8 strings of 8 characters
         * each. Spaces are transparent and any other characters are solid.
         *
         * @param lines The 8 rows of the sprite.
         * @return The generated CharacterSprite.
         */
        public static CharacterSprite make(String... lines) {
            if (lines.length != 8) {
                throw new IllegalArgumentException();
            }

            boolean data[] = new boolean[8 * 8];
            for (int row = 0; row < 8; ++row) {
                String line = lines[row];
                if (line.length() != 8) {
                    throw new IllegalArgumentException();
                }
                for (int col = 0; col < 8; ++col) {
                    data[row * 8 + col] = line.charAt(col) != ' ';
                }
            }
            return new CharacterSprite(data);
        }

    }

}

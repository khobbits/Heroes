package com.herocraftonline.dev.heroes.ui;

/**
 * Structure representing the data a map contains.
 */
public final class MapInfo {

    private int xCenter;
    private int zCenter;
    private byte scale;
    private byte[] data;
    private byte dimension;

    public static int SMALLEST = 0;
    public static int SMALL = 1;
    public static int MEDIUM = 2;
    public static int LARGE = 3;
    public static int LARGEST = 4;

    public MapInfo(byte dimension, int x, int z, byte scale, byte[] data) {
        this.dimension = dimension;
        xCenter = x;
        zCenter = z;
        this.scale = scale;
        this.data = data;
    }

    public int getX() {
        return xCenter;
    }

    public int getZ() {
        return zCenter;
    }

    public byte getScale() {
        return scale;
    }

    public byte[] getData() {
        return data;
    }

    public byte getData(int row, int col) {
        if (row < 0 || col < 0 || row >= 128 || col >= 128) return 0;
        return data[row * 128 + col];
    }

    public void setPosition(int x, int z) {
        xCenter = x;
        zCenter = z;
    }

    public void setScale(byte scale) {
        if (scale < 0) scale = 0;
        if (scale > 4) scale = 4;
        this.scale = scale;
    }

    public void setData(byte[] data) {
        if (data.length != 128 * 128) {
            throw new IllegalArgumentException();
        }
        this.data = data;
    }

    public void setData(int row, int col, byte value) {
        if (row < 0 || col < 0 || row >= 128 || col >= 128) return;
        data[row * 128 + col] = value;
    }

    public void setDimension(byte dimension) {
        this.dimension = dimension;
    }

    public byte getDimension() {
        return this.dimension;
    }
}

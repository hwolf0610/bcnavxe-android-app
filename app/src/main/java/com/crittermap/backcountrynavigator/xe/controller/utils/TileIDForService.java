package com.crittermap.backcountrynavigator.xe.controller.utils;

import java.io.Serializable;

/**
 * Created by nhat@saveondev.com on 1/14/18.
 */

public class TileIDForService implements Serializable {
    int level;
    int x;
    int y;
    private byte[] data;

    public TileIDForService(int level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public TileIDForService(TileID tileID) {
        this.level = tileID.level;
        this.x = tileID.x;
        this.y = tileID.y;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

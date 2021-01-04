package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.annotation.SuppressLint;

import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.mapping.view.Graphic;

import java.io.Serializable;

import static com.crittermap.backcountrynavigator.xe.controller.constant.BC_CONSTANTS.MAX_ZOOM;
import static com.crittermap.backcountrynavigator.xe.controller.constant.BC_CONSTANTS.MIN_TILE_SIZE;
import static com.crittermap.backcountrynavigator.xe.controller.constant.BC_CONSTANTS.MIN_ZOOM;


/**
 * Created by nhat@saveondev.com on 12/19/17.
 */

public class TileID implements Serializable {
    int level;
    int x;
    int y;
    private byte[] data;
    private Graphic graphic;
    private double completeRatio = 0;
    private boolean isChosen = false;
    public TileID(int level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public TileID(TileKey tileKey) {
        this.level = tileKey.getLevel();
        this.x = tileKey.getColumn();
        this.y = tileKey.getRow();
    }

    public TileID(TileIDForService tile) {
        this.level = tile.getLevel();
        this.x = tile.getX();
        this.y = tile.getY();
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

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    public double getCompleteRatio() {
        return completeRatio;
    }

    public void setCompleteRatio(double completeRatio) {
        this.completeRatio = completeRatio;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public TileID copy() {
        return new TileID(this.level, this.x, this.y);
    }

    @Override
    public String toString() {
        return "TileID{" +
                "level=" + level +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public static TileID fromTileIDString(String tileidstring) {
        String[] tokens = tileidstring.split("[ZRC]");
        int level = Integer.parseInt(tokens[1]);
        int y = Integer.parseInt(tokens[2]);
        int x = Integer.parseInt(tokens[3]);
        return new TileID(level, x, y);
    }

    @SuppressLint("DefaultLocale")
    public String getTileIdString() {
        return String.format("Z%dR%dC%d", level, y, x);
    }

    public double getRequireSpaceToDownload() {
        int min = MIN_ZOOM;
        if (this.getLevel() > MIN_ZOOM) {
            min = this.getLevel();
        }
        long numberOfTiles = 0;
        for (int i = min; i <= MAX_ZOOM; i++) {
            numberOfTiles += Math.pow(2, (i - min) * 2);
        }
        return numberOfTiles * (1 - getCompleteRatio()) * MIN_TILE_SIZE / 1024;
    }

    public double getRequireSpaceToDownload(int minZoom, int maxZoom) {
        long numberOfTiles = 0;
        for (int i = minZoom; i <= maxZoom; i++) {
            numberOfTiles += Math.pow(2, (i - minZoom) * 2);
        }
        return numberOfTiles * (1 - getCompleteRatio()) * MIN_TILE_SIZE / 1024;
    }

    public static boolean isEqual(TileID src, TileID des) {
        return src.toString().equals(des.toString());
    }
}

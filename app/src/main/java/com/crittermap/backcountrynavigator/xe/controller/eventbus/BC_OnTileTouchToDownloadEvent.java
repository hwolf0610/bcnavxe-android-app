package com.crittermap.backcountrynavigator.xe.controller.eventbus;

import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;

import java.util.ArrayList;

public class BC_OnTileTouchToDownloadEvent {
    private ArrayList<TileID> ggTile;

    public BC_OnTileTouchToDownloadEvent(ArrayList<TileID> ggTile) {
        this.ggTile = ggTile;
    }

    public ArrayList<TileID> getGgTile() {
        return ggTile;
    }

    public void setGgTile(ArrayList<TileID> ggTile) {
        this.ggTile = ggTile;
    }
}

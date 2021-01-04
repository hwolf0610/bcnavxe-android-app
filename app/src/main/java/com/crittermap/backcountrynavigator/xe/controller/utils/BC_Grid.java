package com.crittermap.backcountrynavigator.xe.controller.utils;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.mapping.view.Graphic;

import java.util.ArrayList;

/**
 * Created by nhat@saveondev.com on 1/6/2018.
 */

public class BC_Grid {
    private ArrayList<TileID> tileIDs = new ArrayList<>();
    private Envelope fullExtend;
    private String name;
    private int id;
    private Graphic graphic;

    public BC_Grid() {
    }

    public ArrayList<TileID> getTileIDs() {
        return tileIDs;
    }

    public void setTileIDs(ArrayList<TileID> tileIDs) {
        this.tileIDs = tileIDs;
    }

    public Envelope getFullExtend() {
        return fullExtend;
    }

    public void setFullExtend(Envelope fullExtend) {
        this.fullExtend = fullExtend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }
}

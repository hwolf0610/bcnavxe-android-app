package com.crittermap.backcountrynavigator.xe.controller.layer;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ImageTiledLayer;

import java.io.ByteArrayOutputStream;

/**
 * Created by valuedcustomer2 on 8/8/18.
 */

public class BC_ShellLayer extends ImageTiledLayer {

    byte[] clearbytes;

    public BC_ShellLayer() {
        super(BC_OnlineMapLayer.CreateTileInfo(1, 25), new Envelope(-20037508.342789244, -20037508.342789244, 20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator()));


        Bitmap.Config config = Bitmap.Config.ARGB_8888;


        Bitmap bitmap = Bitmap.createBitmap(256, 256, config);
        bitmap.eraseColor(Color.argb(0, 0, 0, 0));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        clearbytes = stream.toByteArray();

    }

    @Override
    protected byte[] getTile(TileKey tileKey) {

        return clearbytes;
    }
}

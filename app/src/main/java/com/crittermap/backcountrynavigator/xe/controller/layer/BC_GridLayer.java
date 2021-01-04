package com.crittermap.backcountrynavigator.xe.controller.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_GlobalMapTiles;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.layers.ImageTiledLayer;

import java.io.ByteArrayOutputStream;

public class BC_GridLayer extends ImageTiledLayer {
    private static String TAG = BC_GridLayer.class.getName();
    private Context context;
    private int zoom = 7;
    public BC_GridLayer(Context context, int zoom) {
        super(BC_GlobalMapTiles.createTileInfo(), BC_GlobalMapTiles.getWorldWebExtent());
        this.context = context;
        this.zoom = zoom;
    }

    @Override
    protected byte[] getTile(TileKey tileKey) {
        Bitmap bm = null;
        if (tileKey.getLevel() == zoom) {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.one);
        } else if (tileKey.getLevel() == zoom - 1) {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.four);
        } else if (tileKey.getLevel() == zoom - 2) {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.sixteen);
        } else if (tileKey.getLevel() == zoom - 3) {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.sixtyfour);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }
}

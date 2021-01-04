package com.crittermap.backcountrynavigator.xe.controller.layer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController;
import com.crittermap.backcountrynavigator.xe.controller.constant.LayerType;
import com.crittermap.backcountrynavigator.xe.controller.database.metadata;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_ConverterUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_GlobalMapTiles;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Grid;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.mapping.ArcGISMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nhat@saveondev.com on 1/6/2018.
 */

public class BC_ToggleMBTilesLayer {
    private ArrayList<BC_CoverageGridLayer> layers = new ArrayList<>();
    private SQLiteDatabase mDatabase;
    private TileInfo mTileInfo;

    public BC_ToggleMBTilesLayer(String dbPath, String dbName, metadata metadata) {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {

        }
        mTileInfo = BC_GlobalMapTiles.CreateTileInfo(dbPath, dbName, metadata);
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        String query = "select key_name,key_json from grid_data group by key_name";
        Cursor cursor = mDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            BC_Grid grid = new BC_Grid();
            grid.setName(cursor.getString(0));

            String jsonString = cursor.getString(1);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String fullExtentString = jsonObject.getString("fullExtent");
                Envelope fullExtent = BC_ConverterUtils.getEnvelopFromString(fullExtentString);
                int gridId = Integer.parseInt(jsonObject.getString("gridId"));
                grid.setFullExtend(fullExtent);
                grid.setId(gridId);

                createLayer(grid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    private void createLayer(BC_Grid grid) {
        BC_CoverageGridLayer layer = new BC_CoverageGridLayer(mTileInfo, mDatabase, grid);
        layer.setName(grid.getName());
        layers.add(layer);
    }

    public void showOnMap(ArcGISMap map, BC_ArcGisController controller) {
        for (BC_CoverageGridLayer layer : this.layers) {
            showOnMap(map, layer, controller);
        }
    }

    public BC_CoverageGridLayer showOnMap(ArcGISMap map, int index, BC_ArcGisController controller) {
        BC_CoverageGridLayer layer = this.layers.get(index);
        map.getOperationalLayers().add(layer);
        controller.addLayer(LayerType.SUB_LAYER, layer, true);
        return layer;
    }

    public BC_CoverageGridLayer showOnMap(ArcGISMap map, BC_CoverageGridLayer layer, BC_ArcGisController controller) {
        map.getOperationalLayers().add(layer);
        controller.addLayer(LayerType.SUB_LAYER, layer, true);
        return layer;
    }

    public BC_CoverageGridLayer getLastLayer() {
        return this.layers.get(this.layers.size() - 1);
    }

    public BC_CoverageGridLayer getLayer(int i) {
        return this.layers.get(0);
    }
}

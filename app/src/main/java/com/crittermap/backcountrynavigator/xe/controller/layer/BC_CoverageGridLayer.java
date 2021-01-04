package com.crittermap.backcountrynavigator.xe.controller.layer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_DatabaseHelper;
import com.crittermap.backcountrynavigator.xe.controller.database.metadata;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_ConverterUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_GlobalMapTiles;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Grid;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.ImageTiledLayer;
import com.esri.arcgisruntime.mapping.Viewpoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nhat@saveondev.com on 1/4/18.
 */

public class BC_CoverageGridLayer extends ImageTiledLayer implements Serializable {
    private final String TAG = BC_CoverageGridLayer.class.getSimpleName();
    private SQLiteDatabase mDatabase;
    private BC_DatabaseHelper databaseHelper;
    private String dbName;
    private String dbPath;
    private ArrayList<BC_Grid> mGrid = new ArrayList<>();
    private int mMinZoom = 7;
    private int mMaxZoom = 12;
    private String baseUrl = "";
    private boolean isGMT = true;
    private Viewpoint initialViewpoint;
    private String mapName;

    public BC_CoverageGridLayer(Context context, String dbPath, String dbName, metadata metadata) {
        super(BC_GlobalMapTiles.CreateTileInfo(dbPath, dbName, metadata), BC_GlobalMapTiles.getWorldWebExtent());
        setDbName(dbName);
        setDbPath(dbPath);
        this.baseUrl = metadata.getTemplate();
        databaseHelper = BC_DatabaseHelper.createInstance(dbPath, dbName, metadata);
        mDatabase = databaseHelper.getSqLiteDatabase();
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
                mGrid.add(grid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();

        this.setName(dbName);
    }

    public BC_CoverageGridLayer(BCMap map) {
        super(BC_GlobalMapTiles.CreateTileInfo(BC_Helper.getMBTilesPath(map.getId()), map.getId(), new metadata(map)), BC_GlobalMapTiles.getWorldWebExtent());
        metadata metadata = new metadata(map);
        this.dbName = map.getId();
        this.dbPath = BC_Helper.getMBTilesPath(map.getId());
        this.mMinZoom = map.getMinZoom();
        this.mMaxZoom = map.getMaxZoom();
        this.setName(map.getMapName());
        this.baseUrl = metadata.getTemplate();
        this.databaseHelper = BC_DatabaseHelper.createInstance(dbPath, dbName, metadata);
        this.mDatabase = databaseHelper.getSqLiteDatabase();
        this.initialViewpoint = Viewpoint.fromJson(map.getViewPointJson());
        this.isGMT = map.getTileResolverType().toLowerCase().equals("gmt");
        this.mapName = map.getMapName();

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
                mGrid.add(grid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    public BC_CoverageGridLayer(TileInfo mTileInfo, SQLiteDatabase database, BC_Grid grid) {
        super(mTileInfo, BC_GlobalMapTiles.getWorldWebExtent());
        this.mDatabase = database;
        mGrid.add(grid);
    }

    @Override
    protected byte[] getTile(TileKey tileKey) {
        if (!mDatabase.isOpen()) {
            mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        try {
            TileID tileID = new TileID(tileKey);
            int level = tileID.getLevel();
            int col = tileID.getX();
            int row = tileID.getY();
            // need to flip origin
            String sqlQuery;
            if (!isGMT) {
                int nRows = (1 << level); // Num rows = 2^level
                row = nRows - 1 - row;
            }

            sqlQuery = "SELECT tile_data FROM tiles WHERE zoom_level = " + Integer.toString(level)
                    + " AND tile_column = " + Integer.toString(col) + " AND tile_row = " + Integer.toString(row);

            Log.v(TAG, sqlQuery);

            Cursor imageCur = mDatabase.rawQuery(sqlQuery, null);
            if (imageCur.moveToFirst()) {
                byte[] image = imageCur.getBlob(0);
                imageCur.close();
                return image;
            } else {
                imageCur.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public int getMinZoom() {
        return mMinZoom;
    }

    public void setMinZoom(int mMinZoom) {
        this.mMinZoom = mMinZoom;
    }

    public int getMaxZoom() {
        return mMaxZoom;
    }

    public void setMaxZoom(int mMaxZoom) {
        this.mMaxZoom = mMaxZoom;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public long getExistTilesCount(ArrayList<TileID> listChildTile) {
        return databaseHelper.getExistTilesCount(listChildTile);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isGMT() {
        return isGMT;
    }

    public Viewpoint getInitialViewpoint() {
        return initialViewpoint;
    }

    public String getMapName() {
        return mapName;
    }
}

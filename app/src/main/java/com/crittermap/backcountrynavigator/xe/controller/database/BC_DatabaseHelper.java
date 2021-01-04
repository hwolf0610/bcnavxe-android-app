package com.crittermap.backcountrynavigator.xe.controller.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nhat@saveondev.com on 1/8/18.
 */

public class BC_DatabaseHelper extends BC_AbstractDatabaseHelper {

    private static final String METADATA_TABLE_NAME = "metadata";
    private static final String METADATA_COLUMN_NAME = "name";
    private static final String METADATA_COLUMN_VALUE = "value";

    private static final String GRIDKEY_TABLE_NAME = "grid_key";
    private static final String GRIDKEY_GRID_ID = "grid_id";
    private static final String GRIDKEY_KEY_NAME = "key_name";

    private static final String GRID_UTF_GRID_TABLE_NAME = "grid_utfgrid";
    private static final String GRID_UTF_GRID_GRID_ID = "grid_id";
    private static final String GRID_UTF_GRID_UTF_GRID = "grid_utfgrid";

    private static final String IMAGES_TABLE_NAME = "images";
    private static final String IMAGES_TILE_ID = "tile_id";
    private static final String IMAGES_TILE_DATA = "tile_data";

    private static final String KEYMAP_TABLE_NAME = "keymap";
    private static final String KEYMAP_KEY_NAME = "key_name";
    private static final String KEYMAP_KEY_JSON = "key_json";

    private static final String MAP_TABLE_NAME = "map";
    private static final String MAP_TILE_ID = "tile_id";
    private static final String MAP_GRID_ID = "grid_id";
    private static final String MAP_TILE_ROW = "tile_row";
    private static final String MAP_TILE_COLUMN = "tile_column";
    private static final String MAP_ZOOM_LEVEL = "zoom_level";

    private static final String BEGIN_TRX = "BEGIN TRANSACTION;";

    protected BC_DatabaseHelper(String dbPath, String dbName, metadata metadata) {
        super(dbPath, dbName, metadata);
    }

    static protected ConcurrentHashMap<String,BC_DatabaseHelper> instanceMap = new ConcurrentHashMap<String,BC_DatabaseHelper>();


    static public BC_DatabaseHelper createInstance(String dbPath, String dbName, metadata metadata)
    {

        if(instanceMap.contains(dbPath))
        {
            return instanceMap.get(dbPath);
        }
        else
        {

            BC_DatabaseHelper helper = new BC_DatabaseHelper(dbPath, dbName, metadata);
            instanceMap.put(dbPath,helper);
            return helper;
        }
    }

    @Override
    protected void onCreate(SQLiteDatabase db, metadata metadata) {
        db.beginTransaction();
        String createMetadataTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT,%s TEXT)", METADATA_TABLE_NAME, METADATA_COLUMN_NAME, METADATA_COLUMN_VALUE);
        String createGridKeyTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT,%s TEXT)", GRIDKEY_TABLE_NAME, GRIDKEY_GRID_ID, GRIDKEY_KEY_NAME);
        String createGridUtfGridTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT,%s BLOB)", GRID_UTF_GRID_TABLE_NAME, GRID_UTF_GRID_GRID_ID, GRID_UTF_GRID_UTF_GRID);
        String createImagesTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT,%s BLOB)", IMAGES_TABLE_NAME, IMAGES_TILE_ID, IMAGES_TILE_DATA);
        String createKeyMapTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT,%s TEXT)", KEYMAP_TABLE_NAME, KEYMAP_KEY_NAME, KEYMAP_KEY_JSON);
        String createMapTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT,%s TEXT,%s INTEGER,%s INTEGER,%s INTEGER)", MAP_TABLE_NAME, MAP_TILE_ID, MAP_GRID_ID, MAP_TILE_COLUMN, MAP_TILE_ROW, MAP_ZOOM_LEVEL);
        db.execSQL(createMetadataTableQuery);
        db.execSQL(createGridKeyTableQuery);
        db.execSQL(createGridUtfGridTableQuery);
        db.execSQL(createImagesTableQuery);
        db.execSQL(createKeyMapTableQuery);
        db.execSQL(createMapTableQuery);
        db.execSQL("CREATE UNIQUE INDEX grid_key_lookup ON grid_key (grid_id, key_name)");
        db.execSQL("CREATE UNIQUE INDEX grid_utfgrid_lookup ON grid_utfgrid (grid_id)");
        db.execSQL("CREATE UNIQUE INDEX images_id ON images (tile_id)");
        db.execSQL("CREATE UNIQUE INDEX keymap_lookup ON keymap (key_name)");
        db.execSQL("CREATE UNIQUE INDEX map_index ON map (zoom_level, tile_column, tile_row)");
        db.execSQL("CREATE UNIQUE INDEX name ON metadata (name)");
        db.execSQL("CREATE VIEW grid_data AS SELECT map.zoom_level AS zoom_level, map.tile_column AS tile_column, map.tile_row AS tile_row, keymap.key_name AS key_name, keymap.key_json AS key_json FROM map JOIN grid_key ON map.grid_id = grid_key.grid_id JOIN keymap ON grid_key.key_name = keymap.key_name");
        db.execSQL("CREATE VIEW grids AS SELECT map.zoom_level AS zoom_level, map.tile_column AS tile_column, map.tile_row AS tile_row, grid_utfgrid.grid_utfgrid AS grid FROM map JOIN grid_utfgrid ON grid_utfgrid.grid_id = map.grid_id");
        db.execSQL("CREATE VIEW tiles AS SELECT map.zoom_level AS zoom_level, map.tile_column AS tile_column, map.tile_row AS tile_row, images.tile_data AS tile_data FROM map JOIN images ON images.tile_id = map.tile_id");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('bounds','" + metadata.getBounds() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('center','" + metadata.getCenter() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('minzoom','" + metadata.getMinzoom() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('maxzoom','" + metadata.getMaxzoom() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('name','" + this.getDATABASE_NAME() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('description','" + metadata.getDescription() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('attribution','" + metadata.getAttribution() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('legend','" + metadata.getLegend() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('template','" + metadata.getTemplate() + "');");
        db.execSQL("INSERT INTO `" + METADATA_TABLE_NAME + "` VALUES ('version','" + metadata.getVersion() + "');");
        db.execSQL("COMMIT");
    }

    public boolean isTileExist(TileID tileID) {
        sqLiteDatabase = getSqLiteDatabase();
        String sqlQuery = "SELECT tile_data FROM tiles WHERE zoom_level = " + Integer.toString(tileID.getLevel())
                + " AND tile_column = " + Integer.toString(tileID.getX()) + " AND tile_row = " + Integer.toString(tileID.getY());
        Cursor c = sqLiteDatabase.rawQuery(sqlQuery, null);
        boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    public boolean isTileExistAsync(TileID tileID) {
        String sqlQuery = "SELECT tile_data FROM tiles WHERE zoom_level = " + Integer.toString(tileID.getLevel())
                + " AND tile_column = " + Integer.toString(tileID.getX()) + " AND tile_row = " + Integer.toString(tileID.getY());
        @SuppressLint("Recycle")
        Cursor c = sqLiteDatabase.rawQuery(sqlQuery, null);
        return c.moveToFirst();
    }

    public void insertNewTile(TileID tileID) {

        String id = tileID.getTileIdString();


        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            ContentValues mapValues = new ContentValues();
            mapValues.put(MAP_ZOOM_LEVEL,tileID.getLevel());
            mapValues.put(MAP_TILE_COLUMN,tileID.getX());
            mapValues.put(MAP_TILE_ROW,tileID.getY());
            mapValues.put(MAP_TILE_ID,id);
            sqLiteDatabase.insert(MAP_TABLE_NAME,null,mapValues);

            ContentValues tileValues = new ContentValues();
            tileValues.put(IMAGES_TILE_ID,id);
            tileValues.put(IMAGES_TILE_DATA,tileID.getData());
            sqLiteDatabase.insert(IMAGES_TABLE_NAME,null,tileValues);

            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    public List<TileID> getExistTiles(ArrayList<TileID> listChildTile) {
        ArrayList<TileID> results = new ArrayList<>();
        sqLiteDatabase = getSqLiteDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int idx = 0; idx < listChildTile.size(); idx++) {
            TileID tile = listChildTile.get(idx);
            if (idx > 0) builder.append(",");
            builder.append("'");
            builder.append(tile.getTileIdString());
            builder.append("'");
        }
        builder.append(")");

        String sql = "select zoom_level,tile_column,tile_row from map  where tile_id in " + builder.toString();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int level = cursor.getInt(0);
            int col = cursor.getInt(1);
            int row = cursor.getInt(2);
            TileID tileID = new TileID(level, col, row);
            results.add(tileID);
        }
        cursor.close();
        return results;
    }

    public long getExistTilesCount(ArrayList<TileID> listChildTile) {
        sqLiteDatabase = getSqLiteDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int idx = 0; idx < listChildTile.size(); idx++) {
            TileID tile = listChildTile.get(idx);
            if (idx > 0) builder.append(",");
            builder.append("'");
            builder.append(tile.getTileIdString());
            builder.append("'");
        }
        builder.append(")");
        long count = 0;
        String sql = "select count() from map  where tile_id in " + builder.toString();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void updateTileIDsForVersion126() {
        sqLiteDatabase = getWritableDatabase();
        String selectSQL = "select tile_id,zoom_level,tile_column,tile_row from map";
        Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, null);
        Map<String, String> map = new HashMap<>();
        while (cursor.moveToNext()) {
            String tileID = cursor.getString(0);
            String convertTileID = String.format(Locale.US, "Z%dR%dC%d", cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
            map.put(tileID, convertTileID);
        }
        cursor.close();
        sqLiteDatabase.execSQL(BEGIN_TRX);
        for (String key : map.keySet()) {
            sqLiteDatabase.execSQL(String.format("update map set tile_id = '%s' where tile_id = '%s'", map.get(key), key));
        }
        sqLiteDatabase.execSQL("COMMIT");
        sqLiteDatabase.close();
    }
}

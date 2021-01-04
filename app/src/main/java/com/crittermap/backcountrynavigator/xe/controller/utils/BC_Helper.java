package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by nhat@saveondev.com on 12/23/17.
 */

public class BC_Helper {
    private static final String TAG = BC_Helper.class.getSimpleName();
    public static final String BASE_FOLDER = "BCNAVXE";
    private static final String MAPS_FOLDER = "MAPS";
    private static final String TRIPS_FOLDER = "TRIPS";
    private static final String GENERAL_FOLDER = "GENERAL";
    public static final String TRIP_BD_EXT = ".sqlite";

    private static ArrayList<LevelOfDetail> worldLOD = new ArrayList<>();

    private static void setUpLOD() {
        for (int i = 0; i < 25; i++) {
            double resolution = BC_GlobalMapTiles.resolution(i);
            double scale = resolution * 96 * 39.37;
            LevelOfDetail l = new LevelOfDetail(i, resolution, scale);
            worldLOD.add(l);
        }
    }

    public static double getScaleByZoomLevel(int zoomLevel) {
        List<LevelOfDetail> levelOfDetails = getWorldLOD();
        return levelOfDetails.get(zoomLevel).getScale();
    }


    // based on algorithm found by https://community.esri.com/thread/118146
    public static int getZoomLevelByScale(double scale) {


        long z = Math.round(Math.log(591657550.5 / (scale / 2)) / Math.log(2));

        return (int) z;
        /*
        int zoomLevel = 0;
        List<LevelOfDetail> levelOfDetails = getWorldLOD();
        for (int i = 1; i < levelOfDetails.size(); i++) {
            double nextScale = getScaleByZoomLevel(i);
            if (scale > nextScale) {
                zoomLevel = i - 1;
                break;
            }
        }
        return zoomLevel;
        */
    }

    private static List<LevelOfDetail> getWorldLOD() {
        if (worldLOD.size() == 0) setUpLOD();
        return worldLOD;
    }

    public static String getMapsFolder() {
        return getSubFolderPath(MAPS_FOLDER);
    }

    public static String getSubFolderPath(String folder) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + BASE_FOLDER
                + File.separator + folder;
    }

    public static void createFolder(String folderName) {
        File myDir = new File(getSubFolderPath(folderName));
        if (!myDir.mkdirs()) {
//            Log.i(TAG, "Directory existed or not created");
        }
    }

    public static String getGeoDBPath(String geoDB) {
        createFolder(MAPS_FOLDER);
        File file = new File(getSubFolderPath(MAPS_FOLDER), geoDB + ".geodatabase");
        return file.getAbsolutePath();
    }

    public static String getTripDBPath(String tripDB) {
        createFolder(TRIPS_FOLDER);
        File file = new File(getSubFolderPath(TRIPS_FOLDER), tripDB + TRIP_BD_EXT);
        return file.getAbsolutePath();
    }

    public static String getMBTilesPath(String mbTile) {
        createFolder(MAPS_FOLDER);
        File file = new File(getSubFolderPath(MAPS_FOLDER), mbTile + ".mbtiles");
        return file.getAbsolutePath();
    }

    public static List<String> getMBTiles() {
        createFolder(MAPS_FOLDER);
        ArrayList<String> l = new ArrayList<>();
        File files = new File(getSubFolderPath(MAPS_FOLDER));
        File[] mbtiles = files.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(".mbtiles") && !pathname.getName().contains("journal");
            }
        });
        for (File file : mbtiles) {
            l.add(file.getName());
        }
        return l;
    }


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean isTileValid(TileID tile) {
        return tile.getX() >= 0 && tile.getY() >= 0 && tile.getY() < Math.pow(2, tile.getLevel()) && tile.getX() < Math.pow(2, tile.getLevel());
    }

    public static ArrayList<TileID> createTilesArray(int zoom, int startCol, int startRow, int range) {
        ArrayList<TileID> list = new ArrayList<>();
        for (int i = startCol; i < startCol + range; i++) {
            for (int j = startRow; j < startRow + range; j++) {
                TileID tileID = new TileID(zoom, i, j);
                if (isTileValid(tileID)) {
                    list.add(tileID);
                }
            }
        }
        return list;
    }

    public static boolean isContainsTile(List<TileID> exist, TileID googleTile) {
        return false;
    }

    public static long getMegaBytesAvailableInSdCard() {
        long bytesAvailable;
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = statFs.getAvailableBytes();
            // same as bytesAvailable = statFs.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            bytesAvailable = (long) statFs.getBlockSize() * (long) statFs.getAvailableBlocks();
        }

        return bytesAvailable / (1024 * 1024);
    }

    public static long getMegaBytesTiles(int tilesNumber) {
        return tilesNumber * 64 / 1024;
    }

    public static String getStringFromMegabytes(long mb) {
        return String.format("%d MB", mb);
    }

    public static boolean compareTile(TileID src, TileID des) {
        if (src.getLevel() == des.getLevel()) {
            return src.getX() == des.getX() && src.getY() == des.getY();
        } else {
            if (src.getLevel() > des.getLevel()) {
                return src.getX() > (des.getX() * 2 + 1) || des.getY() > (des.getY() * 2 + 1);
            } else {
                return src.getX() > (des.getX() * 2 + 1) || des.getY() > (des.getY() * 2 + 1);
            }
        }
    }


    private static boolean deleteContents(File file) {
        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles())
                    deleteContents(child);
            }

            return file.delete();
        } catch (Exception ex) {
            Logger.e("deleteContents", ex.getMessage(), ex);
            return false;
        }
    }

    public static File writeResponseBodyToStorage(Response<ResponseBody> response, String tripId) {
        File tripFile = createDBFile(tripId);
        try {
            InputStream inputStream = response.body().byteStream();
            OutputStream outputStream = new FileOutputStream(tripFile);
            byte[] fileReader = new byte[4096];

            int read = inputStream.read(fileReader);

            while (read != -1) {
                outputStream.write(fileReader, 0, read);
                read = inputStream.read(fileReader);
            }

            outputStream.flush();
            return tripFile;
        } catch (Exception e) {
            Logger.e("file utils", e.getMessage(), e);
            return null;
        }
    }

    private static File createDBFile(String fileName) {
        File file = null;
        try {
            createFolder(TRIPS_FOLDER);
            file = new File(getSubFolderPath(TRIPS_FOLDER), fileName + TRIP_BD_EXT);

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            Logger.e("FileUtils", e.getMessage(), e);
        }
        return file;
    }
    
    public static void deleteMap(String mapId) {
        String path = getMBTilesPath(mapId);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}

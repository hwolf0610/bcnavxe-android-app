package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.service.map.BCMapVectorCatalogEntry;
import com.crittermap.backcountrynavigator.xe.service.map.BCVectorAreaLayer;
import com.crittermap.backcountrynavigator.xe.service.map.BCVectorMapCatalogResponse;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valuedcustomer2 on 7/9/18.
 */

public class BC_VectorLoadUtils {

    BCVectorMapCatalogResponse vcatalog;

    HashMap<String, BCMapVectorCatalogEntry> entries = new HashMap<String, BCMapVectorCatalogEntry>();
    int[] ColorChoice = {Color.BLUE, Color.LTGRAY, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.DKGRAY, Color.YELLOW, Color.WHITE};


    public BC_VectorLoadUtils(BCVectorMapCatalogResponse response) {

        vcatalog = response;

        //build a table of blocks
        for (BCMapVectorCatalogEntry entry : response.getAreas()) {
            entries.put(entry.getBlockID(), entry);
        }


    }

    /**
     * Find available block based on Map Center.
     *
     * @param centerPoint
     * @return
     */
    static String getBlockIDforCenter(Point centerPoint) {

        List<String> list = getBlockIDsforCenter(centerPoint);

        for (String s : list) {
            return s;
        }
        return null;
    }

    static Envelope getBlockBounds(String blockID) {
        return BC_GlobalMapTiles.tileBoundsAsEnvelop(TileID.fromTileIDString(blockID));
    }

    public static List<String> getBlockIDsforCenter(Point centerPoint) {
        List<String> blockIDs = new LinkedList<String>();

        //currently checks 6 to 4.
        for (int z = 6; z >= 4; z--) {
            TileID tid = BC_GlobalMapTiles.pointToGoogleTileID(centerPoint, z);
            String blockid = tid.getTileIdString();
            blockIDs.add(blockid);
        }


        return blockIDs;
    }

    public BCMapVectorCatalogEntry getEntry(Point centerPoint) {
        List<String> blockIDs = getBlockIDsforCenter(centerPoint);
        for (String bid : blockIDs) {
            if (entries.containsKey(bid)) {
                return entries.get(bid);
            }
        }
        //nothing here
        return null;
    }

    /**
     * Starts the download of all components needed for an area
     *
     * @param context
     * @param entry
     */
    public void startDownloadOfArea(Context context, BCMapVectorCatalogEntry entry) {

        //make sure we have the style first.
        startDownloadOfStyle(context);

        for (BCVectorAreaLayer layer : entry.getLayers()) {
            startDownloadOf(context, entry.getBlockID(), layer);
        }
    }

    public void startDownloadOfStyle(Context context) {
        String url = "https://crittermap-bc865.firebaseapp.com/style/day/item.json";
        String path = BC_Helper.getSubFolderPath("vcfiles") + "/style/day/item.json";
        File itemFile = new File(path);

        if (!itemFile.exists()) {
            try {
                Date time = Calendar.getInstance().getTime();
//                        ArcGISDownloadRequest request = urlfuture.get();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                request.setDestinationUri(Uri.fromFile(itemFile));
                //request.setDestinationInExternalFilesDir(context, null, "vcfiles/" + vcatalog.getShortname() + "/" + ID + "/" + layer.getLayername() + ".vtpk");
                request.setDescription("Downloading map portion");
                request.setTitle("BackCountry World");
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                manager.enqueue(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        url = "https://crittermap-bc865.firebaseapp.com/style/day/resources.zip";
        path = BC_Helper.getSubFolderPath("vcfiles") + "/style/day/resources.zip";
        itemFile = new File(path);

        if (!itemFile.exists()) {
            try {
                Date time = Calendar.getInstance().getTime();
//                        ArcGISDownloadRequest request = urlfuture.get();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                request.setDestinationUri(Uri.fromFile(itemFile));
                //request.setDestinationInExternalFilesDir(context, null, "vcfiles/" + vcatalog.getShortname() + "/" + ID + "/" + layer.getLayername() + ".vtpk");
                request.setDescription("Downloading map portion");
                request.setTitle("BackCountry World");
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                manager.enqueue(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Will start a download for a particular layer of an area through DownloadManager
     *
     * @param layer
     */
    public void startDownloadOf(Context context, String ID, BCVectorAreaLayer layer) {
        String url = "https://www.arcgis.com/sharing/rest/content/items/" + layer.getID() + "/data";

        Log.i("VectorLoadUtils", "layer: " + layer.toString());
        try {
            Date time = Calendar.getInstance().getTime();
//                        ArcGISDownloadRequest request = urlfuture.get();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            //TODO adjust this for the correctdirectory

            String path = BC_Helper.getSubFolderPath("vcfiles") + "/" + vcatalog.getShortname() + "/" + ID + "/" + layer.getLayername() + ".vtpk";
            File filepath = new File(path);
            Log.i("VectorLoadUtils", "filepath" + filepath.getPath());
            if (filepath.exists())
                filepath.delete();
            Log.i("VectorLoadUtils", "filepath" + filepath.getPath());

            request.setDestinationUri(Uri.fromFile(new File(path)));
            //request.setDestinationInExternalFilesDir(context, null, "vcfiles/" + vcatalog.getShortname() + "/" + ID + "/" + layer.getLayername() + ".vtpk");
            request.setDescription("Downloading map portion");
            request.setTitle("BackCountry World");
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            manager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Find the number of bytes if the block exists on disk.
     *
     * @param BlockID
     * @return -1 if nothing there
     */
    public long blockSize(String BlockID) {
        String path = BC_Helper.getSubFolderPath("vcfiles") + "/" + vcatalog.getShortname() + "/" + BlockID;
        File dir = new File(path);
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {

                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        } else
            return -1;

    }

    public Collection<Graphic> createGraphics() {

        List<Graphic> graphics = new ArrayList<Graphic>();

        for (BCMapVectorCatalogEntry entry : vcatalog.getAreas()) {
            long size = 0;
            for (BCVectorAreaLayer layer : entry.getLayers()) {
                size += layer.getSize();
            }


            String blockID = entry.getBlockID();

            //compare against
            long currentsize = this.blockSize(blockID);

            TileID tid = TileID.fromTileIDString(blockID);
            Envelope env = BC_GlobalMapTiles.tileBoundsAsEnvelop(TileID.fromTileIDString(blockID));
            int colorindex = (tid.getY() % 3) * 3 + (tid.getX() % 3);
            int lc = Color.BLUE;
            int fillcolor = Color.argb(24, Color.red(lc), Color.green(lc), Color.blue(lc));
            if (currentsize >= size) {
                fillcolor = Color.argb(64, 0, 0x4d, 0x40);
            }
            SimpleLineSymbol linesymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, lc, 4f);

            SimpleFillSymbol squareSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, fillcolor, linesymbol);


            Graphic g = new Graphic(env, squareSymbol);


            long sizeInMB = size >> 20;
            g.getAttributes().put("blockID", blockID);
            g.getAttributes().put("size", blockID + ":\n" + sizeInMB + "MB");

            graphics.add(g);

        }

        return graphics;


    }
}




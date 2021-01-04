package com.crittermap.backcountrynavigator.xe.controller.layer;

import android.content.Context;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_VectorLoadUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.esri.arcgisruntime.data.VectorTileCache;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ItemResourceCache;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by valuedcustomer2 on 7/21/18.
 */

public class BC_OfflineVectorLoader {

    static final String TAG = "OfflineVectorLoader";
    static private BC_OfflineVectorLoader _instance;
    BCMap mMap;
    TileID tidLoaded = null;
    List<Layer> layersLoaded = new ArrayList<Layer>();
    /**
     * Based on the current location of the map, make sure you load the appropriate online layers.
     *
     * @param mapview
     */

    HashMap<String, TileID> blocksLoaded = new HashMap<String, TileID>();
    private String stylespath;
    private String mapShortName;
    private String rootpath;

    private BC_OfflineVectorLoader() {
    }

    public static BC_OfflineVectorLoader getInstance() {
        if (_instance == null) {
            _instance = new BC_OfflineVectorLoader();
        }
        return _instance;
    }

    public void setMap(BCMap bcMap, Context ctx) {

        this.clear();
        mMap = bcMap;

        //find the appropriate root path.
        //rootpath = ctx.getExternalFilesDir(null) + "/vcfiles/" + bcMap.getShortName() + "/";
        //stylespath = ctx.getExternalFilesDir(null) + "/vcfiles/" + "styles" + "/";

        rootpath = BC_Helper.getSubFolderPath("vcfiles") + "/" + bcMap.getShortName() + "/";
        stylespath = BC_Helper.getSubFolderPath("vcfiles") + "/" + "style" + "/";
    }

    public void setViewPoints(MapView mapview)
    //Viewpoint centerAndScale, Viewpoint boundingGeometry)
    {
        //do nothing if we aren't setup to do anything.
        if (mMap == null)
            return;

        Viewpoint centerAndScale = mapview.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);


        //find the map center.
        Geometry geometry = centerAndScale.getTargetGeometry();

        if (geometry == null) {
            Log.v(TAG, "Geometry no good.");
            return;
        }

        Point center = (Point) centerAndScale.getTargetGeometry();

        if (center == null) {
            Log.v(TAG, "Center no good. geometry=" + geometry.toJson());
            return;
        }

        //is it in the tile that is loaded, stop
        //if(tidLoaded!=null)
        //{
        //   Envelope bounds = BC_GlobalMapTiles.tileBoundsAsEnvelop(tidLoaded);
        //if center point is in this tile, we are okay - return
        //  if(GeometryEngine.contains(bounds,centerAndScale.getTargetGeometry()))
        //      return;
        //}

        //if there are any previous layers here, lets take them out.
        //for(Layer layer:this.layersLoaded)
        //{
        //   mapview.getMap().getBasemap().getBaseLayers().remove(layer);
        //}


        //this.layersLoaded.clear();

        //find out which grid squares cover our current point.
        List<String> blocklist = BC_VectorLoadUtils.getBlockIDsforCenter(center);

        for (String block : blocklist) {
            Log.v(TAG, "BlockID: " + block);
            if (blocksLoaded.containsKey(block))
                return;

        }

        //check them again


        // Find the most detailed one that exists on disk, if any.

        String targetBlock = mostDetailedBlockIDAvailable(blocklist);


        //if not available we have nothing to load.
        if (targetBlock == null) {
            Log.v(TAG, "N/A");
            return;
        }
        //at this point make sure the baselayers are cleaned.
        Log.v(TAG, "Available: " + targetBlock);


        if (tidLoaded == null) {
            Log.v(TAG, "Clearing " + mapview.getMap().getBasemap().getBaseLayers().size() + " basemap layers.");
            mapview.getMap().getBasemap().getBaseLayers().clear();
        }
        File dir = new File(rootpath + "/" + targetBlock);

        String[] files = dir.list(new VtpkFileFilter());

        Arrays.sort(files);
        Log.v(TAG, "Baselayers count before: " + mapview.getMap().getBasemap().getBaseLayers().size());

        for (String f : files) {
            //now load the layers for it.

            ArcGISVectorTiledLayer vtlayer;

            if (f.toLowerCase().startsWith("map")) {


                VectorTileCache vectorTileCache = new VectorTileCache(rootpath + targetBlock + "/" + f);
                ItemResourceCache itemResourceCache = new ItemResourceCache(stylespath + "day");

                Log.i(TAG, "Loaded layer vectorTileCache:" + vectorTileCache.getPath() + "itemresourcecache: " + itemResourceCache.getPath());

                vtlayer = new ArcGISVectorTiledLayer(vectorTileCache, itemResourceCache);
            } else {
                vtlayer = new ArcGISVectorTiledLayer(rootpath + targetBlock + "/" + f);
                Log.i(TAG, "Loaded layer vectorTileCache:" + rootpath + targetBlock + "/" + f);
            }

            Log.v(TAG, "Loaded vector tile from: " + rootpath + targetBlock + "/" + f);
            mapview.getMap().getBasemap().getBaseLayers().add(vtlayer);

            //add them to the layers loaded.

            //this.layersLoaded.add(vtlayer);
        }
        Log.v(TAG, "Baselayers count after: " + mapview.getMap().getBasemap().getBaseLayers().size());
        tidLoaded = TileID.fromTileIDString(targetBlock);
        this.blocksLoaded.put(targetBlock, tidLoaded);

    }

    private String mostDetailedBlockIDAvailable(List<String> blockIdList) {


        for (String blockID : blockIdList) {
            //if this one is available return it
            File dir = new File(rootpath + blockID);
            if (dir.exists())
                return blockID;
        }


        return null;
    }

    /**
     * Important: call this if you are switching to another kind of map.
     */
    public void clear() {
        mMap = null;
        tidLoaded = null;
        //don't track the layers we've loaded.
        this.blocksLoaded.clear();
    }

    class VtpkFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".vtpk");
        }
    }
}

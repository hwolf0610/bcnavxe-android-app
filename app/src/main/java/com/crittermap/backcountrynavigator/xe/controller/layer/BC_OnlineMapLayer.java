package com.crittermap.backcountrynavigator.xe.controller.layer;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.controller.utils.BC_GlobalMapTiles;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.TTemplateServer;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.ServiceImageTiledLayer;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nhat@saveondev.com on 12/13/17.
 */

public class BC_OnlineMapLayer extends ServiceImageTiledLayer implements IBCLayerInfo {
    private static final String TAG = BC_OnlineMapLayer.class.getSimpleName();
    private final TTemplateServer mTemplateServer;
    private String baseUrl;
    private int mMinZoom;
    private int mMaxZoom;
    private String id;
    private Viewpoint mViewpoint;
    private BCMap mapSource;
    private boolean isStop = false;
    private MapView mMapView;
    private RequestConfiguration mRequestConfiguration;


    public BC_OnlineMapLayer(BCMap map, MapView mapView) {
        super(CreateTileInfo(map.getMinZoom(), map.getMaxZoom()), new Envelope(-20037508.342789244, -20037508.342789244, 20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator()));
        baseUrl = map.getBaseUrl();
        mMinZoom = map.getMinZoom();
        mMaxZoom = map.getMaxZoom();
        mapSource = map;
        id = map.getId();
        mViewpoint = Viewpoint.fromJson(map.getViewPointJson());
        mMapView = mapView;
        mTemplateServer = new TTemplateServer(baseUrl);

        mRequestConfiguration = new RequestConfiguration();

        if (map.getShortName().contains("act")) {
            Map<String, String> headers = new HashMap<>();
            headers.put("referer", "https://www.bcnavxe.com");
            mRequestConfiguration.setHeaders(headers);
        }

        if (mapSource.getShortName().contains("caltopo") || mapSource.getShortName().startsWith("ct") || mapSource.getShortName().startsWith("usforestservice")) {
            try {
                RequestConfiguration.setAdditionalUserAgentInfo("BCN");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.setRequestConfiguration(mRequestConfiguration);
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public static TileInfo CreateTileInfo(int minZoom, int maxZoom)
    {
        int dpi = 96;
        ArrayList<LevelOfDetail> levels = new ArrayList<>();
        for (int i = minZoom; i <= maxZoom; i++)
        {
            double resolution = BC_GlobalMapTiles.resolution(i);
            double scale = resolution * dpi * 39.37;
            LevelOfDetail l = new LevelOfDetail(i, resolution, scale);
            levels.add(l);
        }
        return new TileInfo(dpi, TileInfo.ImageFormat.PNG, levels, new Point(-20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator()), SpatialReferences.getWebMercator(), 256, 256);
    }

    @Override
    protected String getTileUrl(TileKey tileKey) {
        int zoomLevel = BC_Helper.getZoomLevelByScale(mMapView.getMapScale());
        boolean validZoom = (tileKey.getLevel() <= zoomLevel + 1 && tileKey.getLevel() >= zoomLevel - 1);
        if (mMinZoom <= tileKey.getLevel() && tileKey.getLevel() <= mMaxZoom && !isStop && validZoom) {
            TileID tileID = new TileID(tileKey);
            String url = mTemplateServer.getURLforTileID(tileID);
            Log.d(TAG, url);
            return url;
        } else {
            return null;
        }
    }

    @Override
    public String getUri() {
        return baseUrl;
    }

    public int getMinZoom() {
        return mMinZoom;
    }

    public int getMaxZoom() {
        return mMaxZoom;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Viewpoint getViewpoint() {
        return mViewpoint;
    }

    public BCMap getMapSource() {
        return mapSource;
    }

    public void setMapSource(BCMap mapSource) {
        this.mapSource = mapSource;
    }
}

package com.crittermap.backcountrynavigator.xe.controller.layer;

import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.ServiceImageTiledLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by valuedcustomer2 on 6/26/18.
 */

public class BC_WebTiledLayer extends WebTiledLayer implements IBCLayerInfo {
    static final String[] abcdomains = {"a", "b", "c"};
    static final String[] shash = {"0", "1", "2", "3"};
    static final String[] shashplusone = {"1", "2", "3", "4"};
    private static final String TAG = BC_WebTiledLayer.class.getSimpleName();
    private String baseUrl;
    private int mMinZoom;
    private int mMaxZoom;
    private String id;
    private Viewpoint mViewpoint;
    private BCMap mapSource;
    private boolean isStop = false;
    private MapView mMapView;


    protected BC_WebTiledLayer(String templateUri) {
        super(templateUri);


    }

    protected BC_WebTiledLayer(String templateUri, Iterable<String> subDomains) {
        super(templateUri, subDomains);
    }

    /**
     * @return null if WebTiledLayer cannot be used for this template.
     */
    public static ServiceImageTiledLayer create(BCMap map, MapView mapView) {
        String url = map.getBaseUrl();
        String[] subdomains = null;

        //first disqualify the urls we can't use.

        if ((url.contains("{LLBBOX}")) || (url.contains("{MBBOX}")) || (url.contains("{nzy}")) || (url.contains("{quadtree}")))
            return new BC_OnlineMapLayer(map, mapView);

        //now standardize.
        url = url.replaceFirst("\\{l\\}", "{level}");
        url = url.replaceFirst("\\{z\\}", "{level}");
        url = url.replaceFirst("\\{x\\}", "{col}");
        url = url.replaceFirst("\\{y\\}", "{row}");

        if (url.contains("{SHASH}")) {
            url = url.replace("{SHASH}", "{subdomain}");
            subdomains = shash;
        }
        if (url.contains("{SHASHPLUSONE}")) {
            url = url.replace("{SHASH}", "{subdomain}");
            subdomains = shashplusone;
        }
        if (url.contains("{abc}")) {
            url = url.replace("{abc}", "{subdomain}");
            subdomains = abcdomains;
        }

        BC_WebTiledLayer layer;


        if (subdomains != null) {
            ArrayList<String> subdomainList = new ArrayList<String>();
            for (String sub : subdomains
                    ) {
                subdomainList.add(sub);

            }
            layer = new BC_WebTiledLayer(url, subdomainList);
        } else
            layer = new BC_WebTiledLayer(url);

        layer.setNoDataTileBehavior(NoDataTileBehavior.UPSAMPLE);

        layer.baseUrl = map.getBaseUrl();
        layer.mMinZoom = map.getMinZoom();
        layer.mMaxZoom = map.getMaxZoom();
        layer.mapSource = map;
        layer.id = map.getId();
        layer.mViewpoint = Viewpoint.fromJson(map.getViewPointJson());
        layer.mMapView = mapView;


        RequestConfiguration mRequestConfiguration = new RequestConfiguration();

        if (map.getShortName().contains("act")) {
            Map<String, String> headers = new HashMap<>();
            headers.put("referer", "https://www.bcnavxe.com");
            mRequestConfiguration.setHeaders(headers);
        }

        if (map.getShortName().contains("caltopo") || map.getShortName().startsWith("ct") || map.getShortName().startsWith("usforestservice")) {
            try {
                RequestConfiguration.setAdditionalUserAgentInfo("BCN");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        layer.setRequestConfiguration(mRequestConfiguration);


        return layer;
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

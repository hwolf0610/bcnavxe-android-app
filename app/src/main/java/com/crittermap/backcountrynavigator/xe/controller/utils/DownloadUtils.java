package com.crittermap.backcountrynavigator.xe.controller.utils;

import com.crittermap.backcountrynavigator.xe.controller.layer.BC_CoverageGridLayer;
import com.crittermap.backcountrynavigator.xe.controller.layer.IBCLayerInfo;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.List;

public class DownloadUtils {
    public static ArrayList<BC_CoverageGridLayer> getCoverageGridLayersForDownload(MapView mapView) {

        ArrayList<BC_CoverageGridLayer> coverageGridLayers = new ArrayList<>();
        Layer layer = getFirstBaseMap(mapView);
        if (layer != null) {
            if (isSingleLayerMap(mapView)) {
                addLayersForDownload(layer, coverageGridLayers);
            } else {
                for (Layer baseLayer : mapView.getMap().getBasemap().getBaseLayers()) {
                    addLayersForDownload(baseLayer, coverageGridLayers);
                }
            }
        }
        return coverageGridLayers;
    }

    private static boolean isSingleLayerMap(MapView mapView) {
        return mapView.getMap().getBasemap().getBaseLayers().size() == 1;
    }

    private static void addLayersForDownload(Layer layer, List<BC_CoverageGridLayer> coverageGridLayers) {
        if (layer instanceof BC_CoverageGridLayer) {
            BC_CoverageGridLayer coverageGridLayer = (BC_CoverageGridLayer) layer;
            coverageGridLayers.add(coverageGridLayer);
        } else if (layer instanceof IBCLayerInfo) {
            BCMap map = ((IBCLayerInfo) layer).getMapSource();
            BC_CoverageGridLayer coverageGridLayer = new BC_CoverageGridLayer(map);
            coverageGridLayers.add(coverageGridLayer);
        }
    }

    private static Layer getFirstBaseMap(MapView mapView) {
        ArcGISMap map = mapView.getMap();
        if (map != null && map.getBasemap() != null && map.getBasemap().getBaseLayers().size() > 0) {
            return map.getBasemap().getBaseLayers().get(0);
        } else {
            return null;
        }
    }
}

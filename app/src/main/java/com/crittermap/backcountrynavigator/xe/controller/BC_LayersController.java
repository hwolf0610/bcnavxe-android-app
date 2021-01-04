package com.crittermap.backcountrynavigator.xe.controller;

import com.crittermap.backcountrynavigator.xe.controller.constant.LayerType;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Layer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.List;


public class BC_LayersController {

    private List<BC_Layer> layers = new ArrayList<>();

    private MapView mMapView;

    BC_LayersController(MapView mapView) {
        mMapView = mapView;
    }

    public List<BC_Layer> getLayers() {
        return layers;
    }

    void addLayer(LayerType layerType, Object layer, boolean show) {
        addLayer(layerType, layer, "", show);
    }

    void addLayer(LayerType layerType, Object layer, String name, boolean show) {

        switch (layerType) {
            case FEATURE_LAYER:
                for (BC_Layer l : layers) {
                    if (l.getLayerType() == layerType) { //already add user feature to layer controller
                        return;
                    }
                    addFeatureLayer((Layer) layer, 0);
                }
                break;

            case SKETCH:
                addGraphicsOverlay((GraphicsOverlay) layer);
                break;
        }

        if (findLayerByName(name) == null)
            layers.add(new BC_Layer(layerType, layer, name, show));
    }

    void removeLayer(String name) {
        BC_Layer layer = findLayerByName(name);
        if (layer != null) {
            removeLayer(layer);
        }
    }

    void toggleLayer(int index, boolean value) {
        BC_Layer l = layers.get(index);
        changeLayerVisibility(l, value);
    }

    void toggleLayer(String name, boolean value) {
        BC_Layer layer = findLayerByName(name);
        if (layer != null) {
            changeLayerVisibility(layer, value);
        }
    }

    public BC_Layer findLayerByName(String name) {
        for (BC_Layer l : layers) {
            if (l.getName() != null && l.getName().equals(name)) {
                return l;
            }
        }
        return null;
    }

    private void changeLayerVisibility(BC_Layer bc_layer, Boolean isShow) {
        bc_layer.setShow(isShow);

        Object layer = bc_layer.getLayer();
        if (isShow) {
            showLayer(layer);
        } else {
            hideLayer(layer);
        }
    }

    private void showLayer(Object layer) {
        if (layer instanceof GraphicsOverlay) {
            addGraphicsOverlay((GraphicsOverlay) layer);
        } else if (layer instanceof FeatureLayer) {
            addFeatureLayer((Layer) layer, 0);
        }
    }

    private void hideLayer(Object layer) {
        if (layer instanceof GraphicsOverlay) {
            hideGraphicsOverlay((GraphicsOverlay) layer);
        } else if (layer instanceof FeatureLayer) {
            hideFeatureLayer((Layer) layer);
        }
    }

    private void addGraphicsOverlay(GraphicsOverlay graphicsOverlay) {
        if (mMapView.getGraphicsOverlays().indexOf(graphicsOverlay) == -1) {
            if (graphicsOverlay != null) {
                mMapView.getGraphicsOverlays().add(graphicsOverlay);
            }
        }
        if (graphicsOverlay != null && !graphicsOverlay.isVisible()) {
            graphicsOverlay.setVisible(true);
        }
    }

    private void addFeatureLayer(Layer featureLayer, int index) {
        if (featureLayer != null && mMapView.getMap().getOperationalLayers().indexOf(featureLayer) == -1) {
            mMapView.getMap().getOperationalLayers().add(index, featureLayer);
        }
        if (featureLayer != null && !featureLayer.isVisible()) {
            featureLayer.setVisible(true);
        }
    }

    private void hideGraphicsOverlay(GraphicsOverlay graphicsOverlay) {
        if (mMapView.getGraphicsOverlays().indexOf(graphicsOverlay) != -1) {
            if (graphicsOverlay != null && graphicsOverlay.isVisible()) {
                graphicsOverlay.setVisible(false);
            }
        }
    }

    private void hideFeatureLayer(Layer featureLayer) {
        if (mMapView.getMap().getOperationalLayers().indexOf(featureLayer) != -1) {
            if (featureLayer != null && featureLayer.isVisible()) {
                featureLayer.setVisible(false);
            }
        }
    }

    private void removeLayer(BC_Layer layer) {
        layers.remove(layer);
        Object l = layer.getLayer();
        if (l instanceof GraphicsOverlay) {
            removeGraphicOverlay((GraphicsOverlay) l);
        } else if (l instanceof FeatureLayer) {
            removeFeatureLayer((FeatureLayer) l);
        }
    }

    private void removeGraphicOverlay(GraphicsOverlay layer) {
        mMapView.getGraphicsOverlays().remove(layer);
    }

    private void removeFeatureLayer(FeatureLayer layer) {
        mMapView.getMap().getOperationalLayers().remove(layer);
    }

    public boolean contains(GraphicsOverlay graphicsOverlay) {
        for (BC_Layer layer : layers) {
            if (layer.getLayer().equals(graphicsOverlay)) return true;
        }
        return false;
    }

    public String findNameByGraphicsOverlay(GraphicsOverlay graphicsOverlay) {
        for (BC_Layer layer : layers) {
            if (layer.getLayer().equals(graphicsOverlay)) return layer.getName();
        }
        return "";
    }
}

package com.crittermap.backcountrynavigator.xe.controller.layer;

import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.esri.arcgisruntime.mapping.Viewpoint;

/**
 * Created by valuedcustomer2 on 6/27/18.
 */

public interface IBCLayerInfo {

    int getMaxZoom();

    int getMinZoom();

    Viewpoint getViewpoint();

    BCMap getMapSource();

    void setMapSource(BCMap mapSource);
}

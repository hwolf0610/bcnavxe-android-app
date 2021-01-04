package com.crittermap.backcountrynavigator.xe.share;

import android.support.annotation.NonNull;

import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;

import java.util.Iterator;
import java.util.List;

public class IterableArcGISTiledElevationSource implements Iterable<ArcGISTiledElevationSource> {
    private List<ArcGISTiledElevationSource> bList;

    public IterableArcGISTiledElevationSource(List<ArcGISTiledElevationSource> list) {
        this.bList = list;
    }
    @NonNull
    @Override
    public Iterator<ArcGISTiledElevationSource> iterator() {
        return bList.iterator();
    }
}

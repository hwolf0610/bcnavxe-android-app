package com.crittermap.backcountrynavigator.xe.controller.listener;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController;
import com.crittermap.backcountrynavigator.xe.controller.constant.BC_DRAW_MODE;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_FoundGeometryToEditEvent;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_FoundTripToEditEvent;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_CoverageGridLayer;
import com.crittermap.backcountrynavigator.xe.controller.layer.IBCLayerInfo;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Layer;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper.getScaleByZoomLevel;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_NAME;

/**
 * Created by nhat@saveondev.com on 12/16/17.
 */

public class BC_MapTouchListener extends DefaultMapViewOnTouchListener {
    private final String TAG = BC_MapTouchListener.class.getSimpleName();
    private Context mContext;
    private MapView mMapView;
    private BC_ArcGisController mController;
    private Envelope lastV;
    private BCSettings mSettings;

    public BC_MapTouchListener(Context context, MapView mapView, BC_ArcGisController controller, BCSettings settings) {
        super(context, mapView);
        this.mContext = context;
        this.mMapView = mapView;
        this.mController = controller;
        this.mSettings = settings != null ? settings : new BCSettings();
    }

    public void setSettings(BCSettings settings) {
        this.mSettings = settings != null ? settings : new BCSettings();
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent e) {
        Log.d(TAG, "onSingleTapConfirmed");
        try {
            if (mMapView.getMap() != null) {
                android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());

                Point mapPoint = mMapView.screenToLocation(screenPoint);

                if (mController.drawMode != null) {
                    switch (mController.drawMode) {
                        case NONE:
                            selectGraphicAtPointToEdit(screenPoint);
                            break;
                        case SKETCH:
                            selectGraphicAtPointToEdit(screenPoint, mController.getSketchGraphicsOverlay());
                            break;
                        case SELECT_TILES:
                            mController.drawSelectedTile(mapPoint);
                            break;
                        case SELECT_VECTOR_MAPS:
                            mController.confirmVectorDownload(mapPoint);
                            break;
                        case SEARCH:
                            selectGraphicsAtPointForSearch(screenPoint, mController.tempGraphicOverlay);
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.e(TAG, ex.getMessage());
        }

        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mMapView.getVisibleArea() != null) {
            lastV = mMapView.getVisibleArea().getExtent();
        }
        return super.onDown(e);
    }

    @Override
    public boolean onUp(MotionEvent e) {
        if (mMapView.getVisibleArea() != null) {
            Envelope envelope = mMapView.getVisibleArea().getExtent();
            if (!GeometryEngine.equals(lastV, envelope)) {
                switch (mController.drawMode) {
                    case SELECT_TILES:
                        mController.delayToReDraw();
                        break;
                }

            }
        }
        return super.onUp(e);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        super.onScaleEnd(detector);
        Layer layer = null;
        if (mMapView.getMap().getBasemap().getBaseLayers().size() > 0) {
            layer = mMapView.getMap().getBasemap().getBaseLayers().get(0);
        } else if (mMapView.getMap().getOperationalLayers().size() > 0) {
            layer = mMapView.getMap().getOperationalLayers().get(0);
        }
        double minScale = 5.91657527591555E8;
        double maxScale = 70;

        if (layer != null) {
            if (layer instanceof IBCLayerInfo) {
                minScale = getScaleByZoomLevel(((IBCLayerInfo) layer).getMinZoom());
                maxScale = getScaleByZoomLevel(((IBCLayerInfo) layer).getMaxZoom());
            } else if (layer instanceof BC_CoverageGridLayer) {
                minScale = getScaleByZoomLevel(((BC_CoverageGridLayer) layer).getMinZoom());
                maxScale = getScaleByZoomLevel(((BC_CoverageGridLayer) layer).getMaxZoom());
            }
        }
        double currentScale = mMapView.getMapScale();
        if (currentScale > minScale) {
            mMapView.setViewpointScaleAsync(minScale);
        }
    }

    @Override
    public boolean onRotate(MotionEvent event, double rotationAngle) {
        return mSettings.isAllowMapRotation() && super.onRotate(event, rotationAngle);
    }

    private void selectGraphicsAtPointForSearch(android.graphics.Point screenPoint, final GraphicsOverlay graphicsOverlay) {
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mMapView.identifyGraphicsOverlayAsync(graphicsOverlay,
                screenPoint, 10, false);
        //wait for the results to be returned.
        identifyGraphics.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Graphic> graphics = identifyGraphics.get().getGraphics();

                    if (graphics.size() > 0) {
                        for (int i = 0; i < graphicsOverlay.getGraphics().size(); i++) {
                            Graphic graphic = graphicsOverlay.getGraphics().get(i);
                            graphic.setSelected(true);
                        }
                    } else {
                        graphicsOverlay.clearSelection();
                        mController.setDrawMode(BC_DRAW_MODE.NONE);
                        mController.safeClearTempLayer();
                        mMapView.getCallout().dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void selectGraphicsAtPoint(final android.graphics.Point screenPoint) {
        //use the screen location to perform the identify
        for (final BC_Layer l : mController.getLayers()) {
            if (l.getLayer() instanceof GraphicsOverlay) {
                final GraphicsOverlay graphicsOverlay = (GraphicsOverlay) l.getLayer();
                final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mMapView.identifyGraphicsOverlayAsync(graphicsOverlay,
                        screenPoint, 10, false);
                //wait for the results to be returned.
                identifyGraphics.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Graphic> graphics = identifyGraphics.get().getGraphics();
                            String tripId = mController.getTripIdByLayer(graphicsOverlay);
                            String tripName = "";
                            if (graphics.size() > 0) {
                                for (int i = 0; i < graphicsOverlay.getGraphics().size(); i++) {
                                    Graphic graphic = graphicsOverlay.getGraphics().get(i);
                                    graphic.setSelected(true);
                                    tripName = graphic.getAttributes().get(ATTR_TRIP_NAME).toString();
                                }
                                Point mapPoint = mMapView.screenToLocation(screenPoint);
                                EventBus.getDefault().post(new BC_FoundTripToEditEvent(tripId, tripName, graphicsOverlay, mapPoint));
                            } else {
                                graphicsOverlay.clearSelection();
                                EventBus.getDefault().post(new BC_FoundTripToEditEvent(tripId, tripName, null));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private void selectGraphicAtPointToEdit(android.graphics.Point screenPoint) {
        List<BCTripInfo> trips = BCTripInfoDBHelper.loadPinnedTrip();
        boolean hasGeometry = false;
        for (BCTripInfo trip : trips) {
            GraphicsOverlay graphicsOverlay = mController.getTripLayerById(trip.getId());
            if (graphicsOverlay != null && graphicsOverlay.isVisible()) {
                Graphic graphic = selectGraphicAtPointToEdit(screenPoint, graphicsOverlay);
                if (graphic != null) {
                    Point mapPoint = mMapView.screenToLocation(screenPoint);
                    EventBus.getDefault().postSticky(new BC_FoundGeometryToEditEvent(graphic, mapPoint));
                    hasGeometry = true;
                    break;
                }
            }
        }
        if (!hasGeometry) {
            EventBus.getDefault().postSticky(new BC_FoundGeometryToEditEvent(null));
        }
    }

    private Graphic selectGraphicAtPointToEdit(android.graphics.Point screenPoint, final GraphicsOverlay graphicsOverlay) {
        //use the screen location to perform the identify

        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mMapView.identifyGraphicsOverlayAsync(graphicsOverlay,
                screenPoint, 10, false);
        final List<Graphic> graphics;
        try {
            graphics = identifyGraphics.get().getGraphics();
            if (graphics.size() > 0) {
                final Graphic graphic = graphics.get(0);
                graphicsOverlay.clearSelection();
                graphic.setSelected(true);

                if (mController.drawMode == BC_DRAW_MODE.SKETCH) {
                    Callout callout = mMapView.getCallout();
                    TextView tv = new TextView(mContext);
                    tv.setText(R.string.delete_graphics);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new BCRemoveGraphicEvent(graphicsOverlay.getGraphics().indexOf(graphic)));
                        }
                    });
                    callout.setContent(tv);
                    callout.setLocation(graphic.getGeometry().getExtent().getCenter());
                    callout.show();
                } else if (mController.drawMode == BC_DRAW_MODE.NONE) {
                    return graphic;
                }
            } else {
                graphicsOverlay.clearSelection();
                mMapView.getCallout().dismiss();
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}

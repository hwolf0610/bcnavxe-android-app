package com.crittermap.backcountrynavigator.xe.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.location.Criteria;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.constant.BC_DRAW_MODE;
import com.crittermap.backcountrynavigator.xe.controller.constant.LayerType;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_OnTileTouchToDownloadEvent;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_CoverageGridLayer;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_OfflineVectorLoader;
import com.crittermap.backcountrynavigator.xe.controller.listener.BC_MapTouchListener;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_ConverterUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_GlobalMapTiles;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Layer;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_VectorLoadUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.DownloadUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.DrawUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMapDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapVectorCatalogEntry;
import com.crittermap.backcountrynavigator.xe.service.map.BCVectorAreaLayer;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingService;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent;
import com.crittermap.backcountrynavigator.xe.service.trip.di.BCGPSTracking;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.raizlabs.android.dbflow.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

import static com.crittermap.backcountrynavigator.xe.controller.constant.BC_CONSTANTS.MAX_ZOOM;
import static com.crittermap.backcountrynavigator.xe.controller.constant.BC_CONSTANTS.MIN_ZOOM;
import static com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper.getScaleByZoomLevel;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_NAME;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_TYPE;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.PAUSE;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.STOP;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.TRACKING;
import static com.crittermap.backcountrynavigator.xe.share.BCUtils.drawableToBitmap;

/**
 * Created by nhat@saveondev.com on 12/13/17.
 */

public class BC_ArcGisController {

    //region PARAMETERS
    public static final String DB_TRACKING_TEMP = "tracking_temp";
    /**
     * Constant used in the location settings dialog.
     */
    public static final String FILE_ANDROID_ASSET_WAYPOINTS = "file:///android_asset/waypoints/";
    private static final int DEFAULT_ZOOM_LEVEL = 7;
    private final String TAG = BC_ArcGisController.class.getName();
    private LocationDisplay mLocationDisplay;
    public BC_DRAW_MODE drawMode = BC_DRAW_MODE.NONE;
    public Activity mActivity;
    /**
     * Store temporary user graphics
     */
    public SketchEditor sketchEditor = new SketchEditor();
    public SketchCreationMode selectedSketchCreateMode;
    public int selectedColor = Color.GREEN;
    public GraphicsOverlay tempGraphicOverlay;
    private SimpleFillSymbol simpleFillSymbol;
    public ArrayList<TileID> pickedTilesList = new ArrayList<>();
    private Context mContext;
    private SimpleLineSymbol simpleLineSymbol;
    private GraphicsOverlay sketchGraphicsOverlay;
    private int defaultLineWidth = 3;
    private int defaultSymbolWidth = 12;
    private long minTimeToGetNewLocation = 1000; //miliseconds
    private long minDistanceToGetNewLocation = 1; //meters
    /**
     * Store user trek graphics
     */
    private SimpleMarkerSymbol simpleMarkerSymbol;
    private MapView mMapView;

    private BC_LayersController mLayerController;
    private ArrayList<BC_CoverageGridLayer> mCurrentSelectedLayerList = new ArrayList<>();
    private int tempMinZoom = 0;
    private BC_MapTouchListener bc_mapTouchListener;
    private ActivityInteractor activityInteractor;
    private double lastScale = 0;
    private Handler handler;
    static final String labelSquares = "{" +
            "\"labelExpressionInfo\": {" + // Define a labeling expression that will show the address attribute value
            "\"expression\": \"return $feature.size;\"}," +
            "\"labelPlacement\": \"esriServerPolygonPlacementAlwaysHorizontal\"," + // Align labels horizontally
            "\"symbol\": {" + // Use a green bold text symbol
            "\"color\": [0,255,50,255]," +
            "\"font\": {\"size\": 12, \"weight\": \"bold\"}," +
            "\"type\": \"esriTS\"}" +
            "}";


    static final String labelPolygon = "{" +
            "\"labelExpressionInfo\": {" + // Define a labeling expression that will show the address attribute value
            "\"expression\": \"return $feature.geometryName;\"}," +
            "\"labelPlacement\": \"esriServerPolygonPlacementAlwaysHorizontal\"," + // Align labels horizontally
            //"\"where\": \"TypeOf($feature)==Polygon\"}," +
            "\"symbol\": {" + // Use a black bold text symbol
            "\"color\": [0,0,0,255]," +
            "\"haloColor\": [255,255,255,96]," +
            "\"haloSize\": 2," +

            "\"font\": {\"size\": 10}," +
            "\"type\": \"esriTS\"}" +
            "}";

    static final String labelPolyline = "{" +
            "\"labelExpressionInfo\": {" + // Define a labeling expression that will show the address attribute value
            "\"expression\": \"return $feature.geometryName;\"}," +
            //"\"where\": \"TypeOf($feature)==Polyline\"}," +
            "\"labelPlacement\": \"esriServerLinePlacementBelowAlong\"," + // Align labels horizontally
            "\"symbol\": {" + // Use a black bold text symbol
            "\"color\": [0,0,0,255]," +
            "\"haloColor\": [255,255,255,96]," +
            "\"haloSize\": 2," +
            "\"font\": {\"size\": 10}," +
            "\"type\": \"esriTS\"}" +
            "}";

    static final String labelPoint = "{" +
            "\"labelExpressionInfo\": {" + // Define a labeling expression that will show the address attribute value
            "\"expression\": \"return $feature.geometryName;\"}," +
            //"\"where\": \"TypeOf($feature)==Point\"}," +
            "\"labelPlacement\": \"esriServerPointLabelPlacementBelowCenter\"," + // Align labels horizontally
            "\"symbol\": {" + // Use a black bold text symbol
            "\"color\": [0,0,0,255]," +
            "\"haloColor\": [255,255,255,96]," +
            "\"haloSize\": 2," +
            "\"font\": {\"size\": 10}," +
            "\"type\": \"esriTS\"}" +
            "}";


    //Injection
    private ThreadExecutor threadExecutor;
    private PostExecutionThread postExecutionThread;

    //endregion


    @Inject
    public BC_ArcGisController(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    public void setup(Activity activity, final MapView mapView, final SketchDrawListener listener) {
        this.mContext = activity;
        this.mActivity = activity;
        this.mMapView = mapView;

        simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, defaultLineWidth);
        simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(32, 0, 255, 255), simpleLineSymbol);
        simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, Color.RED, defaultSymbolWidth);

        mMapView.setSketchEditor(sketchEditor);
        setSelectedColor(selectedColor);
        sketchEditor.addGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {
                if (drawMode == BC_DRAW_MODE.SKETCH)
                    listener.onSketchEditorChange(sketchEditor.canUndo(), sketchEditor.canRedo());
            }
        });

        sketchEditor.removeGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {
                if (drawMode == BC_DRAW_MODE.SKETCH)
                    listener.onSketchEditorChange(sketchEditor.canUndo(), sketchEditor.canRedo());
            }
        });

        mLocationDisplay = mMapView.getLocationDisplay();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        AndroidLocationDataSource androidLocationDataSource = new AndroidLocationDataSource(mContext, criteria, minTimeToGetNewLocation, minDistanceToGetNewLocation);
        mLocationDisplay.setLocationDataSource(androidLocationDataSource);

        mLayerController = new BC_LayersController(mapView);
        setUpArcgisLocationService();
    }
    @SuppressLint("MissingPermission")
    public BC_ArcGisController(Context context, Activity activity, final MapView mapView, final SketchDrawListener listener) {
        this.mContext = context;
        this.mActivity = activity;
        this.mMapView = mapView;

        simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, defaultLineWidth);
        simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(32, 0, 255, 255), simpleLineSymbol);
        simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, Color.RED, defaultSymbolWidth);

        mMapView.setSketchEditor(sketchEditor);
        setSelectedColor(selectedColor);
        sketchEditor.addGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {
                if (drawMode == BC_DRAW_MODE.SKETCH)
                    listener.onSketchEditorChange(sketchEditor.canUndo(), sketchEditor.canRedo());
            }
        });

        sketchEditor.removeGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {
                if (drawMode == BC_DRAW_MODE.SKETCH)
                    listener.onSketchEditorChange(sketchEditor.canUndo(), sketchEditor.canRedo());
            }
        });

        mLocationDisplay = mMapView.getLocationDisplay();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        AndroidLocationDataSource androidLocationDataSource = new AndroidLocationDataSource(mContext, criteria, minTimeToGetNewLocation, minDistanceToGetNewLocation);
        mLocationDisplay.setLocationDataSource(androidLocationDataSource);

        mLayerController = new BC_LayersController(mapView);
        setUpArcgisLocationService();
    }

    public void delayToReDraw() {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                double currentScale = mMapView.getMapScale();
                if (lastScale != currentScale) {
                    lastScale = currentScale;
                    handler.removeCallbacksAndMessages(null);
                    delayToReDraw();
                } else {
                    safeClearTempLayer();
                    enterMode(BC_DRAW_MODE.SELECT_TILES, tempMinZoom, true, true);
                    for (TileID tileID : pickedTilesList) {
                        safeCheckTempLayer().getGraphics().add(tileID.getGraphic());
                    }
                }
            }
        };
        handler.postDelayed(runnable, 100);
    }

    //region SETUP
    private void setUpArcgisLocationService() {
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                if (dataSourceStatusChangedEvent.isStarted())
                    return;

                if (dataSourceStatusChangedEvent.getError() == null)
                    return;

                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void navigateToMyLocation(final double scale, final float duration, final OnSuccessListener<Location> doneListener) {

        BCGPSTracking.getInstance().getLastLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    mMapView.setViewpointAsync(new Viewpoint(location.getLatitude(), location.getLongitude(), scale), duration)
                            .addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    doneListener.onSuccess(location);
                                }
                            });
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, e.getLocalizedMessage());
                doneListener.onSuccess(null);
            }
        });
    }

    public void navigateToMyLocation(@NonNull OnSuccessListener<Location> onSuccessListener, double zoomLevel) {
        try {
            navigateToMyLocation(zoomLevel, 1, onSuccessListener);
        } catch (Exception e) {
            onSuccessListener.onSuccess(null);
        }
    }

    public void navigateToMyLocation(@NonNull OnSuccessListener<Location> onSuccessListener) {
        try {
            navigateToMyLocation(getScaleByZoomLevel(DEFAULT_ZOOM_LEVEL), 1, onSuccessListener);
        } catch (Exception e) {
            onSuccessListener.onSuccess(null);
        }
    }

    public void navigateToBookmark(final BCBookmarkData bookmarkData, boolean isOffline) {
        final Point point = new Point(Double.parseDouble(bookmarkData.getLon()), Double.parseDouble(bookmarkData.getLat()), SpatialReferences.getWgs84());
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(mMapView.getContext(),
                R.drawable.point);
        final ListenableFuture<PictureMarkerSymbol> p = PictureMarkerSymbol.createAsync(drawable);
        p.addDoneListener(new Runnable() {
            @Override
            public void run() {
                PictureMarkerSymbol pictureMarkerSymbol;
                try {
                    pictureMarkerSymbol = p.get();
                    pictureMarkerSymbol.setHeight(40);
                    pictureMarkerSymbol.setWidth(40);
                    Graphic graphic =
                            new Graphic(point, pictureMarkerSymbol);


                    Graphic trickGraphic = new Graphic(point, pictureMarkerSymbol);
                    TextView tv = new TextView(mContext);
                    tv.setTextColor(ContextCompat.getColor(mContext,R.color.textTab));
                    tv.setText(bookmarkData.getBookmarkName());
                    drawCalloutWithGraphic(graphic, trickGraphic, null, tv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mMapView.setViewpointAsync(new Viewpoint(point.getExtent()));

        //load map by bookmark
        String mapName = bookmarkData.getBasemapType();
        BCMap map = BCMapDBHelper.getByShortName(mapName);

        activityInteractor.loadMapToArcGisMap(map);
    }
    //endregion

    //region GETTERS & SETTERS
    public void setDrawMode(BC_DRAW_MODE drawMode) {
        safeClearTempLayer();
        this.drawMode = drawMode;
    }

    public String getTripIdByLayer(GraphicsOverlay graphicsOverlay) {
        return this.mLayerController.findNameByGraphicsOverlay(graphicsOverlay);
    }

    public GraphicsOverlay getTripLayerById(String tripId) {
        BC_Layer layer = this.mLayerController.findLayerByName(tripId);
        if (layer != null && layer.getLayer() instanceof GraphicsOverlay) {
            return (GraphicsOverlay) layer.getLayer();
        } else {
            return null;
        }
    }

    public ListenableFuture navigateToLocationNoZoom(Point point) {
        return navigateToLocation(new Viewpoint(point, mMapView.getMapScale()));
    }

    private ListenableFuture navigateToLocation(Viewpoint viewpoint) {
        return mMapView.setViewpointAsync(viewpoint);
    }

    public void zoomIn(final ImageView zoomInBtn) {
        try {
            double current = mMapView.getMapScale();
            mMapView.setViewpointScaleAsync(current / 2).addDoneListener(new Runnable() {
                @Override
                public void run() {
                    zoomInBtn.setEnabled(true);
                    switch (drawMode) {
                        case SELECT_TILES:
                            delayToReDraw();
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zoomOut(final ImageView zoomOutBtn) {
        try {
            double current = mMapView.getMapScale();
            zoomOutBtn.setEnabled(false);
            mMapView.setViewpointScaleAsync(current * 2).addDoneListener(new Runnable() {
                @Override
                public void run() {
                    zoomOutBtn.setEnabled(true);
                    switch (drawMode) {
                        case SELECT_TILES:
                            delayToReDraw();
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void configTouchOnMap(BCSettings settings) {
        if (bc_mapTouchListener == null) {
            bc_mapTouchListener = new BC_MapTouchListener(mMapView.getContext(), mMapView, this, settings);
        } else {
            bc_mapTouchListener.setSettings(settings);
        }
        mMapView.setOnTouchListener(bc_mapTouchListener);
    }

    public MapView getMapView() {
        return mMapView;
    }

    public GraphicsOverlay getSketchGraphicsOverlay() {
        return safeCheckSketchLayer();
    }

    public List<BC_Layer> getLayers() {
        return mLayerController.getLayers();
    }
    //endregion

    //region DRAW
    public void setSelectedColor(int color) {
        this.selectedColor = color;

        simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, selectedColor, defaultLineWidth);
        simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, selectedColor, simpleLineSymbol);
        simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, selectedColor, defaultSymbolWidth);

        SketchStyle sketchStyle = new SketchStyle();
        sketchStyle.setLineSymbol(simpleLineSymbol);
        sketchStyle.setFillSymbol(simpleFillSymbol);
        sketchStyle.setVertexSymbol(simpleMarkerSymbol);
        sketchStyle.setSelectedVertexSymbol(simpleMarkerSymbol);

        sketchEditor.setSketchStyle(sketchStyle);
    }

    public void clearSketchEditor() {
        if (sketchEditor.isSketchValid()) {
            sketchEditor.clearGeometry();
            sketchEditor.stop();
        }
    }

    public void resetSketchEditor() {
        simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, selectedColor, defaultLineWidth);
        simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, selectedColor, simpleLineSymbol);
        simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, selectedColor, defaultSymbolWidth);

        SketchStyle sketchStyle = new SketchStyle();
        sketchStyle.setLineSymbol(simpleLineSymbol);
        sketchStyle.setFillSymbol(simpleFillSymbol);
        sketchStyle.setVertexSymbol(simpleMarkerSymbol);
        sketchStyle.setSelectedVertexSymbol(simpleMarkerSymbol);

        sketchEditor.setSketchStyle(sketchStyle);
    }

    public void setSketchEditorMode(String value) {
        switch (value) {
            case "Polyline":
                selectedSketchCreateMode = SketchCreationMode.POLYLINE;
                break;
            case "Polygon":
                selectedSketchCreateMode = SketchCreationMode.POLYGON;
                break;
            case "Point":
                selectedSketchCreateMode = SketchCreationMode.POINT;
                break;
            case "Multiple Points":
                selectedSketchCreateMode = SketchCreationMode.MULTIPOINT;
                break;
            case "Freehand Polyline":
                selectedSketchCreateMode = SketchCreationMode.FREEHAND_LINE;
                break;
            case "Freehand Polygon":
                selectedSketchCreateMode = SketchCreationMode.FREEHAND_POLYGON;
                break;
        }
        sketchEditor.start(selectedSketchCreateMode);
    }

    public Single<PictureMarkerSymbol> setSketchEditorModeForWaypoint(final String resStr) {
        return Single.create(new SingleOnSubscribe<PictureMarkerSymbol>() {
            @Override
            public void subscribe(final SingleEmitter<PictureMarkerSymbol> emitter) {
                selectedSketchCreateMode = SketchCreationMode.POINT;
                try {
                    PictureDrawable pictureDrawable = Glide.with(mContext).as(PictureDrawable.class).load(Uri.parse(FILE_ANDROID_ASSET_WAYPOINTS + resStr))
                            .listener(new RequestListener<PictureDrawable>() {

                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<PictureDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(PictureDrawable resource, Object model, Target<PictureDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(24, 24).get();
                    Bitmap bm = drawableToBitmap(pictureDrawable);
                    BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), bm);
                    final ListenableFuture<PictureMarkerSymbol> p = PictureMarkerSymbol.createAsync(drawable);
                    p.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            PictureMarkerSymbol pictureMarkerSymbol;
                            try {
                                pictureMarkerSymbol = p.get();
                                pictureMarkerSymbol.setHeight(40);
                                pictureMarkerSymbol.setWidth(40);
                                emitter.onSuccess(pictureMarkerSymbol);
                            } catch (Exception e) {
                                emitter.onError(e);
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    emitter.onError(e);
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(threadExecutor.getScheduler())
                .observeOn(postExecutionThread.getScheduler());
    }

    public void startSketchEditorWithPicture(PictureMarkerSymbol pictureMarkerSymbol, Point point) {
        SketchStyle sketchStyle = new SketchStyle();
        sketchStyle.setVertexSymbol(pictureMarkerSymbol);
        sketchStyle.setSelectedVertexSymbol(pictureMarkerSymbol);
        sketchEditor.setSketchStyle(sketchStyle);
        sketchEditor.start(point);
    }

    public void undo() {
        if (sketchEditor.canUndo()) {
            sketchEditor.undo();
        }
    }

    public void redo() {
        if (sketchEditor.canRedo()) sketchEditor.redo();
    }

    public void save() throws IllegalAccessException {
        DrawUtils.save(sketchEditor);
    }

    public void draw(BCTripInfo trip, BCGeometry bcGeometry, final GraphicsOverlay graphicsOverlay) {
        mLayerController.addLayer(LayerType.SKETCH, graphicsOverlay, StringUtils.isNullOrEmpty(bcGeometry.getTripId()) ? "NA" : bcGeometry.getTripId(), true);
        DrawUtils.draw(trip, bcGeometry, graphicsOverlay, mContext);

    }

    public Graphic getShapeGraphicFromBCGeometry(BCTripInfo trip, BCGeometry bcGeometry) {
        return DrawUtils.getShapeGraphicFromBCGeometry(trip, bcGeometry);
    }

    private Graphic drawEnvelop(Envelope envelope, SimpleLineSymbol lineSymbol, boolean isUserSketchLayer) {
        try {
            GraphicsOverlay graphicsOverlay;
            if (isUserSketchLayer) {
                graphicsOverlay = safeCheckSketchLayer();
            } else {
                graphicsOverlay = safeCheckTempLayer();
            }

            Graphic graphic = new Graphic(envelope, lineSymbol);
            graphicsOverlay.getGraphics().add(graphic);

            return graphic;
        } catch (Exception e) {
            return null;
        }
    }

    private GraphicsOverlay safeCheckSketchLayer() {
        if (sketchGraphicsOverlay == null) {
            sketchGraphicsOverlay = new GraphicsOverlay();
            sketchGraphicsOverlay.setSelectionColor(Color.GREEN);
            mMapView.getGraphicsOverlays().add(sketchGraphicsOverlay);
            mLayerController.addLayer(LayerType.SKETCH, sketchGraphicsOverlay, true);
        }

        return sketchGraphicsOverlay;
    }

    private GraphicsOverlay safeCheckTempLayer() {
        if (tempGraphicOverlay == null) {
            tempGraphicOverlay = new GraphicsOverlay();
            tempGraphicOverlay.setSelectionColor(Color.YELLOW);
            mMapView.getGraphicsOverlays().add(0, tempGraphicOverlay);
        }

        return tempGraphicOverlay;
    }

    @SuppressLint("DefaultLocale")
    public void stopDraw(int maxZoom) {
        switch (drawMode) {
            case SELECT_TILES:
            case AUTO_SELECT_TILES:
            case AUTO_SELECT_TILES_BY_TRAILS:
                if (pickedTilesList.size() > 0) {
                    ArrayList<BC_CoverageGridLayer> coverageGridLayers = DownloadUtils.getCoverageGridLayersForDownload(mMapView);
                    if (coverageGridLayers.size() > 0) {
                        for (BC_CoverageGridLayer coverageGridLayer : coverageGridLayers) {
                            if (tempMinZoom > coverageGridLayer.getMinZoom() && tempMinZoom < coverageGridLayer.getMaxZoom()) {
                                coverageGridLayer.setMinZoom(tempMinZoom);
                                coverageGridLayer.setMaxZoom(maxZoom);

                            }
                        }
                        BC_TilesDownloadService.startActionTilesDownload(mContext, pickedTilesList, coverageGridLayers, MapUtils.getDefaultOrLastMap().getMapName(), coverageGridLayers.get(0).getMinZoom() - 2, coverageGridLayers.get(0).getMaxZoom());
                    }
                }
                pickedTilesList.clear();
                tempGraphicOverlay.getGraphics().clear();
                break;
        }

        mMapView.getCallout().dismiss();
        drawMode = null;
    }

    public void drawCalloutWithGraphic(Graphic graphic, Graphic trickGraphic, View.OnClickListener listener, View callOutView) {
        safeCheckTempLayer();
        callOutView.setOnClickListener(listener);
        Callout mCallout = mMapView.getCallout();
        Callout.ShowOptions options = new Callout.ShowOptions(true, false, false);
        mCallout.setShowOptions(options);
        Point center = trickGraphic.getGeometry().getExtent().getCenter();
        mCallout.setLocation(center);
        mCallout.setContent(callOutView);
        mCallout.show();

        if (!tempGraphicOverlay.getGraphics().contains(graphic)) {
            tempGraphicOverlay.getGraphics().add(graphic);
        }
    }

    public void drawCallout(Graphic graphic, View callOutView, Point tapPoint) {
        GeometryType geometryType = graphic.getGeometry().getGeometryType();
        Callout mCallout = mMapView.getCallout();
        Point center;
        switch (geometryType) {
            default:
                mMapView.getCallout().show(callOutView, tapPoint);
                break;
            case POINT:
                center = graphic.getGeometry().getExtent().getCenter();
                mCallout.setLocation(center);
                mCallout.setContent(callOutView);
                mCallout.show();
                break;
        }
    }

    public void safeClearTempLayer() {
        GraphicsOverlay graphicsOverlay = safeCheckTempLayer();
        if (graphicsOverlay.getSelectedGraphics().size() == 0) {
            graphicsOverlay.getGraphics().clear();
        }
    }

    public void safeClearSketchLayer() {
        GraphicsOverlay graphicsOverlay = safeCheckSketchLayer();
        if (graphicsOverlay.getSelectedGraphics().size() == 0) {
            graphicsOverlay.getGraphics().clear();
        }
    }

    /**
     * This function will change mode for drawing
     *
     * @param draw_mode        :                  related to BC_DRAW_MODE
     * @param gridZoom         :
     * @param isChangeViewPort : When in SELECT_TILE drawMode, when user change view port, we need to re-draw the grid
     * @param isDrawGrid
     */
    public void enterMode(BC_DRAW_MODE draw_mode, int gridZoom, boolean isChangeViewPort, boolean isDrawGrid) {
        if (mActivity instanceof BCHomeActivity) {
            ((BCHomeActivity) mActivity).showProgress("Entering draw mode");
        }
        if (mMapView.getMap() == null) {
            Toast.makeText(mContext, "There is no map on view", Toast.LENGTH_SHORT).show();
            return;
        }

        this.drawMode = draw_mode;

        tempMinZoom = gridZoom;

        safeClearTempLayer();

        int currentZoom = BC_Helper.getZoomLevelByScale(mMapView.getMapScale());

        Envelope visibleAreaEnvelope = mMapView.getVisibleArea().getExtent();
        ArrayList<BC_CoverageGridLayer> coverageGridLayers = DownloadUtils.getCoverageGridLayersForDownload(mMapView);

        if (coverageGridLayers.size() > 0) {
            if (currentZoom > 4 && gridZoom - currentZoom < 3) {
                List<Envelope> envelopes = BC_GlobalMapTiles.getGoogleTilesByZoom(visibleAreaEnvelope, gridZoom, 0);
                for (Envelope e : envelopes) {
                    //get tile       ratio
                    Point wp = (Point) GeometryEngine.project(e.getCenter(), SpatialReferences.getWebMercator());
                    TileID tile = BC_GlobalMapTiles.metersToTmsTile(wp.getX(), wp.getY(), tempMinZoom);
                    TileID googleTile = BC_GlobalMapTiles.tmsToGoole(tile);

                    ArrayList<TileID> listChildTile = BC_GlobalMapTiles.getGoogleTilesToDownload(googleTile, MIN_ZOOM, MAX_ZOOM);
                    displayCompleteRatioOfTile(e, googleTile, listChildTile, coverageGridLayers);
                }
            }

            if (isDrawGrid) {
                List<Envelope> envelopeList = BC_GlobalMapTiles.getGoogleTilesToDrawGridByZoom(visibleAreaEnvelope, tempMinZoom, 2);
                for (Envelope envelopeToDraw : envelopeList) {
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, currentZoom / 2);
                    drawEnvelop(envelopeToDraw, lineSymbol, false);
                }
            }
        } else {
            Toast.makeText(mContext, "Map is not loaded", Toast.LENGTH_SHORT).show();
        }
        if (mActivity instanceof BCHomeActivity) {
            ((BCHomeActivity) mActivity).dismissProgress();
        }
    }


    private void displayCompleteRatioOfTile(Envelope e, TileID googleTile, ArrayList<TileID> listChildTile, ArrayList<BC_CoverageGridLayer> layers) {
        if (tempMinZoom >= MIN_ZOOM) { //only calculate complete ratio for above min_zoom
            long size = 0;
            for (BC_CoverageGridLayer lastLayer : layers) {
                if (!lastLayer.isGMT()) {
                    listChildTile = BC_ConverterUtils.convertTileIdList(listChildTile);
                }
                size += lastLayer.getExistTilesCount(listChildTile);
            }

            double ratio = (double) size / ((double) listChildTile.size() * layers.size());

            SimpleFillSymbol simpleFillSymbol;
            if (ratio == 0) {
                simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(0, 0, 0, 0), null);
            } else if (ratio == 1) {
                simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(100, 0, 255, 0), null);
            } else {
                simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb((int) (ratio * 200), 255, 255, 0), null);
            }
            drawPolygon(e, simpleFillSymbol, false);
        }
    }
    //endregion

    @SuppressLint("DefaultLocale")
    public void drawSelectedTile(Point p) {
        DrawUtils.drawSelectedTile(p, pickedTilesList, safeCheckTempLayer(), tempMinZoom);

        EventBus.getDefault().post(new BC_OnTileTouchToDownloadEvent(pickedTilesList));
    }

    private Graphic drawPolygon(Envelope envelope, SimpleFillSymbol simpleFillSymbol, boolean isUseSketchLayer) {
        return drawPolygon(envelope, simpleFillSymbol, isUseSketchLayer, false);
    }

    private Graphic drawPolygon(Envelope envelope, SimpleFillSymbol simpleFillSymbol, boolean isUseSketchLayer, boolean reverse) {
        GraphicsOverlay graphicsOverlay = isUseSketchLayer ? safeCheckSketchLayer() : safeCheckTempLayer();
        return DrawUtils.drawPolygon(envelope, simpleFillSymbol, graphicsOverlay, reverse);
    }

    //region LAYER CONTROLLER
    public void toggleLayer(int index, boolean value) {
        mLayerController.toggleLayer(index, value);
    }

    public void toggleLayer(String name, boolean value) {
        mLayerController.toggleLayer(name, value);
    }

    public void addLayer(LayerType layerType, GraphicsOverlay graphicsOverlay, String tripId, boolean show) {
        mLayerController.addLayer(layerType, graphicsOverlay, tripId, show);
    }

    public void addLayer(LayerType layerType, Layer onlineMapLayer, boolean show) {
        mLayerController.addLayer(layerType, onlineMapLayer, null, show);
    }

    public void removeLayer(String name) {
        mLayerController.removeLayer(name);
    }

    public Object findLayerByName(String name) {
        return mLayerController.findLayerByName(name);
    }
    //endregion

    //region GET_TRIP_FOR_TRACKING
    public void startTracking() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        navigateToMyLocationByArcgis();
        Intent trackingService = new Intent(mContext, BCTrackingService.class);
        if (!BCUtils.isTrackingServiceRunning(BCTrackingService.class, mContext)) {
            mContext.startService(trackingService);
        } else {
            EventBus.getDefault().post(new BCTrackingStatusChangedEvent(TRACKING));
        }
    }

    public void pauseTracking() {
        EventBus.getDefault().post(new BCTrackingStatusChangedEvent(PAUSE));
    }

    public void stopTracking() {
        EventBus.getDefault().post(new BCTrackingStatusChangedEvent(STOP));
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackingUpdate(final BCTrackingService.LocationChangedEvent locationChangedEvent) {
        Log.e(TAG, "onTrackingUpdate");
        safeCheckSketchLayer();
        DrawUtils.drawTrackingLine(locationChangedEvent.getPolyline(), sketchGraphicsOverlay);
    }

    public void navigateToMyLocationByArcgis() {
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        if (!mLocationDisplay.isStarted())
            mLocationDisplay.startAsync();
    }

    public void drawTrackingLines() {
        safeCheckSketchLayer();
        DrawUtils.drawTrackingLines(sketchGraphicsOverlay, mContext);
    }

    public boolean isVectorMap(MapView mMapView) {
        for (Layer l : mMapView.getMap().getBasemap().getBaseLayers()) {
            if (l instanceof ArcGISVectorTiledLayer)
                return true;
        }

        return false;
    }

    public void setActivityInteractor(ActivityInteractor interactor) {
        this.activityInteractor = interactor;
    }

    private BC_VectorLoadUtils loadUtils;

    public void onViewPointChangedAfterNavigation() {
        BC_OfflineVectorLoader loader = BC_OfflineVectorLoader.getInstance();

        loader.setViewPoints(this.getMapView());
    }

    public void drawVectorSquares(BC_VectorLoadUtils lUtils) {
        this.setDrawMode(BC_DRAW_MODE.SELECT_VECTOR_MAPS);

        this.loadUtils = lUtils;


        GraphicsOverlay overlay = safeCheckTempLayer();
        overlay.setLabelsEnabled(true);
        overlay.getGraphics().addAll(loadUtils.createGraphics());
        overlay.getLabelDefinitions().add(LabelDefinition.fromJson(labelSquares));


    }

    public void confirmVectorDownload(Point mapPoint) {

        Point center = mapPoint;

        final BCMapVectorCatalogEntry entry = loadUtils.getEntry(center);
        //create a dialog with the information

        if (entry == null)
            return;


        String message = "Download block " + entry.getBlockID() + ": \n";
        long size = 0;

        for (BCVectorAreaLayer vlayer : entry.getLayers()) {
            size += vlayer.getSize();

        }

        message += "  " + size / (1024 * 1024) + "MB\n";
        long currentsize = loadUtils.blockSize(entry.getBlockID());
        if (currentsize > 0) {
            message += " You have: " + currentsize / (1024 * 1024) + "MB\n";
        }

        BCAlertDialogHelper.showAlertWithConfirm(mContext, BCAlertDialogHelper.BCDialogType.DOWNLOAD_VECTOR_MAP, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    loadUtils.startDownloadOfArea(mContext, entry);
                }

            }
        }, null, message);


        //if they agree


    }
    //endregion


    /**
     * Factory method replaces using a constructor for trip graphicsoverlays.
     * @return
     */
    public static GraphicsOverlay createTripGraphicsOverlay()
    {
        GraphicsOverlay overlay = new GraphicsOverlay();


        overlay.setLabelsEnabled(true);

        //overlay.getLabelDefinitions().add(LabelDefinition.fromJson(labelPolygon));
        overlay.getLabelDefinitions().add(LabelDefinition.fromJson(labelPolyline));
        overlay.getLabelDefinitions().add(LabelDefinition.fromJson(labelPoint));

        return overlay;
    }


    public interface SketchDrawListener {
        void onSketchEditorChange(boolean canUndo, boolean canRedo);
    }

    public interface ActivityInteractor {
        void loadMapToArcGisMap(BCMap map);
    }
}

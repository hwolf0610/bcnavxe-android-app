package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_OnTileTouchToDownloadEvent;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.raizlabs.android.dbflow.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.DB_TRACKING_TEMP;
import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.FILE_ANDROID_ASSET_WAYPOINTS;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_ID;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_NAME;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_TYPE;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_URL;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_ID;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_NAME;
import static com.crittermap.backcountrynavigator.xe.share.BCUtils.drawableToBitmap;

public class DrawUtils {

    private static final String TAG = DrawUtils.class.getSimpleName();

    public static void drawTrackingLines(GraphicsOverlay sketchGraphicsOverlay, Context context) {
        try {
            BCGeometryDAO geometryDAO = new BCGeometryDAO(DB_TRACKING_TEMP);
            List<BCGeometry> geometries = geometryDAO.getAll();
            SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(0, 0, 0), 5);
            BCTripInfo tempTrip = new BCTripInfo();
            for (BCGeometry geometry : geometries) {
                if (geometry.getDesc() == null) {
                    draw(tempTrip, geometry, sketchGraphicsOverlay, context);
                } else {
                    Graphic graphic = new Graphic(Geometry.fromJson(geometry.getGeoJSON()), simpleLineSymbol);
                    graphic.getAttributes().put(TripUtils.ATTR_TRACKING, true);
                    sketchGraphicsOverlay.getGraphics().add(graphic);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void draw(BCTripInfo trip, BCGeometry bcGeometry, final GraphicsOverlay graphicsOverlay, Context context) {

        try {


            final Geometry geometry = Geometry.fromJson(bcGeometry.getGeoJSON());

            //TODO width, fillstyle, linestyle, pointstyle @Nhat
            Graphic graphic;
            String imageUrl = bcGeometry.getImageUrl();
            if (bcGeometry.getType().toLowerCase().equals("point") && !StringUtils.isNullOrEmpty(imageUrl)) {
                drawWayPointFromGeometry(trip, bcGeometry, geometry, graphicsOverlay, context);
                return;
            }

            graphic = getShapeGraphicFromBCGeometry(trip, bcGeometry);
            graphicsOverlay.getGraphics().add(graphic);
        } catch (ArcGISRuntimeException ex) {
            Log.w(TAG, "parsing geometry " + bcGeometry.getName() + " : " + bcGeometry.getGeoJSON(), ex);
        }
    }

    public static Graphic getShapeGraphicFromBCGeometry(BCTripInfo trip, BCGeometry bcGeometry) {
        Graphic graphic;
        Geometry geometry = Geometry.fromJson(bcGeometry.getGeoJSON());
        switch (bcGeometry.getType().toLowerCase()) {
            case "point":
            case "multipoint":
                graphic = drawPointFromGeometry(trip, bcGeometry, geometry);
                break;
            case "line":
            case "polyline":
                graphic = drawLineFromGeometry(bcGeometry, geometry);
                break;
            case "polygon":
                graphic = drawPolygonFromGeometry(bcGeometry, geometry);
                break;
            default:
                Symbol symbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                        bcGeometry.getColor(), 3);
                graphic = new Graphic(geometry, symbol);
                break;
        }

        graphic.getAttributes().put(ATTR_TRIP_ID, bcGeometry.getTripId());
        graphic.getAttributes().put(ATTR_TRIP_NAME, trip.getName());
        graphic.getAttributes().put(ATTR_GEO_ID, bcGeometry.getId());
        graphic.getAttributes().put(ATTR_GEO_NAME, bcGeometry.getName());
        graphic.getAttributes().put(ATTR_GEO_TYPE, bcGeometry.getType());
        graphic.getAttributes().put(ATTR_GEO_URL, bcGeometry.getImageUrl());

        return graphic;
    }

    public static void save(SketchEditor sketchEditor) throws IllegalAccessException {
        if (sketchEditor.isSketchValid() && sketchEditor.getGeometry() != null && !sketchEditor.getGeometry().isEmpty()) {

            sketchEditor.getGeometry();
            BCGeometry bcGeometry = new BCGeometry();
            bcGeometry.setGeoJSON(sketchEditor.getGeometry().toJson());
            bcGeometry.setColor(sketchEditor.getSketchStyle().getSelectionColor());
            //TODO set other values: width, pointstyle, fillstyle, linestyle,color fill @Nhat
            BCTrip trip = new BCTrip();

            BC_TripsDBHelper.saveLocalTrip(trip, Collections.singletonList(bcGeometry));
        }
    }

    public static void drawTrackingLine(Polyline polyline, GraphicsOverlay sketchGraphicsOverlay) {
        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(0, 0, 0), 5);
        Graphic graphic = new Graphic(polyline, simpleLineSymbol);
        graphic.getAttributes().put(TripUtils.ATTR_TRACKING, true);
        sketchGraphicsOverlay.getGraphics().add(graphic);
    }

    public static Graphic drawPolygon(Envelope envelope, SimpleFillSymbol simpleFillSymbol, GraphicsOverlay graphicsOverlay, boolean reverse) {
        PointCollection points = BC_ConverterUtils.getPointCollectionFromEnvelop(envelope);
        Polygon polygon = new Polygon(points);
        Graphic graphic = new Graphic(polygon, simpleFillSymbol);
        if (reverse) {
            graphicsOverlay.getGraphics().add(0, graphic);
        } else {
            graphicsOverlay.getGraphics().add(graphic);
        }

        return graphic;
    }

    public static void drawSelectedTile(Point p, ArrayList<TileID> pickedTilesList, GraphicsOverlay graphicsOverlay, int tempMinZoom) {
        Point mP = (Point) GeometryEngine.project(p, SpatialReferences.getWebMercator());
        TileID ggTile = BC_GlobalMapTiles.tmsToGoole(BC_GlobalMapTiles.metersToTmsTile(mP, tempMinZoom));
        Envelope envelope = BC_ConverterUtils.getEnvelopFromGoogleTile(ggTile);
        int idx = -1;
        for (TileID tileID : pickedTilesList) {
            if (TileID.isEqual(tileID, ggTile)) {
                idx = pickedTilesList.indexOf(tileID);
                break;
            }
        }

        if (idx != -1) {
            TileID tileID = pickedTilesList.get(idx);
            pickedTilesList.remove(tileID);
            graphicsOverlay.getGraphics().remove(tileID.getGraphic());
        } else {
            Graphic g = drawPolygon(envelope, new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(50, 0, 0, 255), null),
                    graphicsOverlay, true);
            ggTile.setGraphic(g);
            pickedTilesList.add(ggTile);
        }

        EventBus.getDefault().post(new BC_OnTileTouchToDownloadEvent(pickedTilesList));
    }

    private static void drawWayPointFromGeometry(final BCTripInfo trip, final BCGeometry bcGeometry, final Geometry geometry, final GraphicsOverlay graphicsOverlay, final Context context) {
        Log.d(TAG, "Drawing drawWayPointFromGeometry");
        String resStr = bcGeometry.getImageUrl() + ".svg";
        Glide.with(context)
                .as(PictureDrawable.class)
                .load(Uri.parse(FILE_ANDROID_ASSET_WAYPOINTS + resStr))
                .into(new SimpleTarget<PictureDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull PictureDrawable resource, @Nullable Transition<? super PictureDrawable> transition) {
                        Bitmap bm = drawableToBitmap(resource);
                        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bm);
                        final ListenableFuture<PictureMarkerSymbol> p = PictureMarkerSymbol.createAsync(drawable);
                        p.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                PictureMarkerSymbol pictureMarkerSymbol;
                                try {
                                    pictureMarkerSymbol = p.get();
                                    pictureMarkerSymbol.setHeight(40);
                                    pictureMarkerSymbol.setWidth(40);

                                    Graphic g = new Graphic();
                                    g.setGeometry(geometry);
                                    g.setSymbol(pictureMarkerSymbol);

                                    g.getAttributes().put(ATTR_TRIP_ID, bcGeometry.getTripId());
                                    g.getAttributes().put(ATTR_TRIP_NAME, trip.getName());
                                    g.getAttributes().put(ATTR_GEO_ID, bcGeometry.getId());
                                    g.getAttributes().put(ATTR_GEO_NAME, bcGeometry.getName());
                                    g.getAttributes().put(ATTR_GEO_TYPE, bcGeometry.getType());
                                    g.getAttributes().put(ATTR_GEO_URL, bcGeometry.getImageUrl());
                                    graphicsOverlay.getGraphics().add(g);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
    }

    public static Observable<BitmapDrawable> getWaypointPictureMarkerSymbolFromString(final Context context, String resStr) {
        Log.d(TAG, "Drawing drawWayPointFromGeometry");
        resStr = resStr.concat(".svg");
        final String finalResStr = resStr;
        return Observable.create(new ObservableOnSubscribe<BitmapDrawable>() {
            @Override
            public void subscribe(final ObservableEmitter<BitmapDrawable> emitter) {
                Glide.with(context)
                        .as(PictureDrawable.class)
                        .load(Uri.parse(FILE_ANDROID_ASSET_WAYPOINTS + finalResStr))
                        .into(new SimpleTarget<PictureDrawable>() {
                            @Override
                            public void onResourceReady(@NonNull PictureDrawable resource, @Nullable Transition<? super PictureDrawable> transition) {
                                try {
                                    Bitmap bm = drawableToBitmap(resource);
                                    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bm);
                                    emitter.onNext(drawable);
                                } catch (Exception e) {
                                    emitter.onError(e);
                                } finally {
                                    emitter.onComplete();
                                }
                            }
                        });
            }
        });
    }

    private static Graphic drawPointFromGeometry(BCTripInfo trip, BCGeometry bcGeometry, final Geometry geometry) {
        Log.d(TAG, "Drawing drawPointFromGeometry");
        SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE,
                bcGeometry.getColor(), 8);

        Graphic graphic = new Graphic(geometry, simpleMarkerSymbol);
        graphic.getAttributes().put(ATTR_TRIP_ID, bcGeometry.getTripId());
        graphic.getAttributes().put(ATTR_TRIP_NAME, trip.getName());
        graphic.getAttributes().put(ATTR_GEO_ID, bcGeometry.getId());
        graphic.getAttributes().put(ATTR_GEO_NAME, bcGeometry.getName());
        graphic.getAttributes().put(ATTR_GEO_TYPE, bcGeometry.getType());
        graphic.getAttributes().put(ATTR_GEO_URL, bcGeometry.getImageUrl());
        return graphic;
    }

    private static Graphic drawLineFromGeometry(BCGeometry bcGeometry, Geometry geometry) {
        Log.d(TAG, "Drawing drawLineFromGeometry");
        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                bcGeometry.getColor(), 3);
        return new Graphic(geometry, simpleLineSymbol);
    }

    private static Graphic drawPolygonFromGeometry(BCGeometry bcGeometry, Geometry geometry) {
        Log.d(TAG, "Drawing drawPolygonFromGeometry");
        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                bcGeometry.getColor(), 3);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS,
                bcGeometry.getColor(), simpleLineSymbol);
        return new Graphic(geometry, simpleFillSymbol);
    }
}

package com.crittermap.backcountrynavigator.xe.service.trip;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTrackingPointDAO;
import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrackingPoint;
import com.crittermap.backcountrynavigator.xe.service.trip.di.BCGPSTracking;
import com.crittermap.backcountrynavigator.xe.service.trip.di.DaggerGPSTrackingComponent;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.DB_TRACKING_TEMP;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.PAUSE;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.STOP;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.TRACKING;
import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.share.BC_StatsUtils.updateStatistics;

public class BCTrackingService extends Service implements BCGPSTracking.LocationChangedCallback {
    private static final String TAG = BCTrackingService.class.getSimpleName();
    private BCGeometryDAO tempGeometryDAO;
    private int groupId = 0;
    private Point mPreviousPoint;
    private long startTime = -1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Inject
    BCGPSTracking bcgpsTracking;

    @Inject
    GetUserSettingsUseCase getUserSettingsUseCase;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        DaggerGPSTrackingComponent.builder().applicationModule(BCApp.getInstance().getApplicationModule()).build().inject(this);
        bcgpsTracking.setLocationCallback(this);
        getUserSettingsUseCase.execute(new Consumer<SettingsDTO>() {
            @Override
            public void accept(SettingsDTO settingsDTO) {
                BCSettings settings = new BCSettings();
                if (settingsDTO != null) {
                    settings.importFromDTO(settingsDTO);
                    bcgpsTracking.startLocationUpdate(settings);
                } else {
                    Log.e(TAG, "Cannot get settings");
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable o) {
                Log.e(TAG, o.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLocationChanged(final Location location) {

        Log.e("BCTrackingService", "onLocationChanged");
        if (location == null) return;

        if (mPreviousPoint == null) {
            mPreviousPoint = Point.createWithM(location.getLongitude(), location.getLatitude(), location.getAltitude(), location.getTime(), SpatialReferences.getWgs84());
            return;
        }

        Point currentLocation = Point.createWithM(location.getLongitude(), location.getLatitude(), location.getAltitude(), location.getTime(), SpatialReferences.getWgs84());

        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        points.add(mPreviousPoint);
        points.add(currentLocation);

        final Polyline polyline = new Polyline(points);

        saveTempTrackingTrip(polyline);
        saveTrackingPoint(mPreviousPoint);

        EventBus.getDefault().post(new LocationChangedEvent(polyline));

        final Point finalPreviousPoint = (Point) GeometryEngine.project(mPreviousPoint, mPreviousPoint.getSpatialReference());
        final Point finalCurrentPoint = (Point) GeometryEngine.project(currentLocation, currentLocation.getSpatialReference());
        final Polyline finalPolyline = (Polyline) GeometryEngine.project(polyline, polyline.getSpatialReference());
        getUserSettingsUseCase.execute(new Consumer<SettingsDTO>() {
            @Override
            public void accept(SettingsDTO settingsDTO) {
                BCSettings settings = new BCSettings();
                settings.importFromDTO(settingsDTO);
                updateStatistics(finalPolyline, finalPreviousPoint, finalCurrentPoint, location, settings);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable o) {
                Log.e(TAG, o.getMessage());
            }
        });

        mPreviousPoint = currentLocation;
    }

    @Subscribe
    public void onTrackingStatusChanged(BCTrackingStatusChangedEvent event) {
        switch (event.getTrackingStatus()) {
            case TRACKING:
                bcgpsTracking.setLocationCallback(this);
                getUserSettingsUseCase.execute(new Consumer<SettingsDTO>() {
                    @Override
                    public void accept(SettingsDTO settingsDTO) {
                        BCSettings settings = new BCSettings();
                        if (settingsDTO != null) {
                            settings.importFromDTO(settingsDTO);
                            bcgpsTracking.startLocationUpdate(settings);
                        } else {
                            Log.e(TAG, "Cannot get settings");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable o) {
                        Log.e(TAG, o.getMessage());
                    }
                });

                BCUtils.saveTrackingStatus(TRACKING);
                if (startTime < 0) {
                    startTime = Calendar.getInstance().getTimeInMillis();
                }
                break;
            case PAUSE:
                bcgpsTracking.stopLocationUpdate();
                BCUtils.saveTrackingStatus(PAUSE);
                saveTrackingPoint(mPreviousPoint);
                mPreviousPoint = null;
                groupId++;
                break;
            case STOP:
                bcgpsTracking.stopLocationUpdate();
                BCUtils.saveTrackingStatus(STOP);
                saveTrackingPoint(mPreviousPoint);
                mPreviousPoint = null;
                groupId = 0;
                break;

        }

    }

    private void saveTrackingPoint(Point point) {
        BCTrackingPoint trackingPoint = new BCTrackingPoint(UUID.randomUUID().toString(), point.toJson(), groupId);
        new BCTrackingPointDAO(DB_TRACKING_TEMP).insertOrUpdate(trackingPoint);
    }

    private void saveTempTrackingTrip(Polyline polyline) {
        if (tempGeometryDAO == null) {
            tempGeometryDAO = new BCGeometryDAO(DB_TRACKING_TEMP);
        }

        BCGeometry bcGeometry = new BCGeometry();
        bcGeometry.setGeoJSON(polyline.toJson());
        bcGeometry.setWidth(5);
        bcGeometry.setType("polyline");
        bcGeometry.setDesc("tracking");
        bcGeometry.setTimestamp(Calendar.getInstance().getTimeInMillis());
        bcGeometry.setColor(Color.argb(255, 0, 0, 0));

        tempGeometryDAO.insertOrUpdate(bcGeometry);
    }

    public static class LocationChangedEvent {
        private Polyline polyline;

        LocationChangedEvent(Polyline polyline) {
            this.polyline = polyline;
        }


        public Polyline getPolyline() {
            return polyline;
        }

        public void setPolyline(Polyline polyline) {
            this.polyline = polyline;
        }
    }
}

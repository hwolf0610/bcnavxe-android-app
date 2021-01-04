package com.crittermap.backcountrynavigator.xe.data.model.trip_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometry;
import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometryEnvelope;
import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometryMultipoint;
import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometryPoint;
import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometryPolygon;
import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometryPolyline;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.crittermap.backcountrynavigator.xe.ui.saveTrip.BCSaveTripActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.converter.gson.GsonConverterFactory;

import static com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged.DrawTripEvent.PIN;
import static com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged.DrawTripEvent.REPLACE;
import static com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper.BCDialogType.PIN_TRIP_ALERT;

/**
 * Created by thutrang.dao on 4/6/18.
 */

public class TripUtils {
    public static final String POINT = "point";
    public static final String POLYLINE = "polyline";
    public static final String POLYGON = "polygon";
    public static final String MULTIPOINT = "multipoint";

    public static final String ATTR_TRIP_ID = "tripId";
    public static final String ATTR_TRIP_NAME = "tripName";
    public static final String ATTR_GEO_ID = "geometryId";
    public static final String ATTR_GEO_NAME = "geometryName";
    public static final String ATTR_GEO_TYPE = "geometryType";
    public static final String ATTR_GEO_URL = "imageUrl";
    public static final String ATTR_TRACKING = "tracking";
    public static final String ATTR_SAVED = "saved";
    public static final String ATTR_GEO_ATTITUDE = "geometryAttitude";

    public static final String SEARCH_RESULT = "search_result";

    protected static final int MAX_PINNED_TRIP = 3;

    public static BCTripInfo map(BCTripResponse response) {
        BCTripInfo tripInfo = new BCTripInfo();
        tripInfo.setId(response.getId());
        tripInfo.setName(response.getName());
        tripInfo.setOwnerId(response.getUserName());
        tripInfo.setTripStatus(TripStatus.NOT_DOWNLOADED);
        tripInfo.setTrekFolder(response.getTrekFolder());
        tripInfo.setTimestamp(BCUtils.parseDate(response.getUpdatedAt()));
        tripInfo.setLastSync(Calendar.getInstance().getTimeInMillis());
        return tripInfo;
    }

    public static List<BCTripInfo> mapAll(List<BCTripResponse> tripResponses) {
        List<BCTripInfo> tripInfos = new ArrayList<>();
        for (BCTripResponse tripResponse : tripResponses) {
            tripInfos.add(map(tripResponse));
        }
        return tripInfos;
    }

    public static Map<String, BCTripInfo> toHashMap(List<BCTripInfo> infos) {
        Map<String, BCTripInfo> maps = new HashMap<>();
        for (BCTripInfo tripInfo : infos) {
            maps.put(tripInfo.getId(), tripInfo);
        }
        return maps;
    }

    public static BCTripResponse fromJson(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(BCTripGeometry.class, new BCTripGeometryDeserializer());
        return gsonBuilder.create().fromJson(json, BCTripResponse.class);
    }

    public static GsonConverterFactory buildTripResponseConverterFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(BCTripResponse.class, new BCTripGeometryDeserializer());
        return GsonConverterFactory.create(gsonBuilder.create());
    }

    public static class BCTripGeometryDeserializer implements JsonDeserializer<BCTripGeometry> {
        @Override
        public BCTripGeometry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            BCTripGeometry BCTripGeometry = null;

            switch (type) {
                case "multipoint":
                    BCTripGeometry = new BCTripGeometryMultipoint();
                    break;
                case "point":
                    BCTripGeometry = new BCTripGeometryPoint();
                    break;
                case "polyline":
                    BCTripGeometry = new BCTripGeometryPolyline();
                    break;
                case "polygon":
                    BCTripGeometry = new BCTripGeometryPolygon();
                    break;
                case "extent":
                    BCTripGeometry = new BCTripGeometryEnvelope();
                    break;

            }
            if (BCTripGeometry != null) {
                BCTripGeometry.setType(type);
                BCTripGeometry.deserialize(jsonObject.get("coordinates"));
            }
            return BCTripGeometry;
        }
    }

    public static void actionPinTrip(BCTripInfo tripInfo, Context context) {
        actionPinTrip(tripInfo, context, null);
    }

    public static void actionPinTrip(BCTripInfo tripInfo, Context context, BCSaveTripActivity.PinTripCallback callback) {
        List<BCTripInfo> pinnedTrips = BCTripInfoDBHelper.loadPinnedTrip();
        if (pinnedTrips.size() >= MAX_PINNED_TRIP && !isExist(pinnedTrips, tripInfo)) {
            showPinTripAlert(pinnedTrips, tripInfo, context, callback);
        } else {
            tripInfo.setPinned(true);
            tripInfo.setShowedChecked(true);
            BCTripInfoDBHelper.update(tripInfo);
            EventBus.getDefault().postSticky(new BCEventPinTripChanged(tripInfo, PIN));
            if (callback!= null) {
                callback.onSave();
            }
        }
    }

    private static boolean isExist(List<BCTripInfo> trips, BCTripInfo trip) {
        for (BCTripInfo bcTripInfo : trips) {
            if (bcTripInfo.getId().equals(trip.getId())) return true;
        }
        return false;
    }

    private static void showPinTripAlert(final List<BCTripInfo> pinnedTrips, final BCTripInfo newPinTrip, Context context, final BCSaveTripActivity.PinTripCallback callback) {
        BCAlertDialogHelper.showTripActionDialog(context, PIN_TRIP_ALERT,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BCTripInfo repalceTrip = pinnedTrips.get(which);
                        repalceTrip.setPinned(false);
                        repalceTrip.setShowedChecked(false);
                        newPinTrip.setPinned(true);
                        BCTripInfoDBHelper.update(repalceTrip);
                        BCTripInfoDBHelper.update(newPinTrip);

                        BCEventPinTripChanged event = new BCEventPinTripChanged(repalceTrip, REPLACE);
                        event.setNewTripInfo(newPinTrip);

                        EventBus.getDefault().postSticky(event);
                        if (callback != null) callback.onSave();

                    }
                }, pinnedTrips);
    }

    @SuppressLint("DefaultLocale")
    public static String createTripNamePrefix() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%02d%02d%02d%02d%02d%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}

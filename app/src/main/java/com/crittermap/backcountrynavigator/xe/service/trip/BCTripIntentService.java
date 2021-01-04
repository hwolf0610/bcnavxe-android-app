package com.crittermap.backcountrynavigator.xe.service.trip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class BCTripIntentService extends IntentService {
    private static final String ACTION_GET_TRIPS = "com.crittermap.backcountrynavigator.xe.service.trip.ACTION_GET_TRIPS";
    private static final String ACTION_DOWNLOAD_TRIPS = "com.crittermap.backcountrynavigator.xe.service.trip.ACTION_DOWNLOAD_TRIPS";
    private static final String EXTRA_DATA = "com.crittermap.backcountrynavigator.xe.service.trip.DATA";

    public BCTripIntentService() {
        super(BCTripIntentService.class.getSimpleName());
    }

    public static void startGetAllTripsByUser(Context context, String userName) {
        if (TextUtils.isEmpty(userName)) return;
        Intent intent = new Intent(context, BCTripIntentService.class);
        intent.setAction(ACTION_GET_TRIPS);
        intent.putExtra(EXTRA_DATA, userName);
        context.startService(intent);
    }

    public static void startDownloadTrip(Context context, String tripId) {
        if (TextUtils.isEmpty(tripId)) return;
        Intent intent = new Intent(context, BCTripIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_TRIPS);
        intent.putExtra(EXTRA_DATA, tripId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            assert action != null;
            switch (action) {
                case ACTION_GET_TRIPS:
                    handleActionGetTrips(intent.getStringExtra(EXTRA_DATA));
                    break;
                case ACTION_DOWNLOAD_TRIPS:
                    handleActionDownloadTrip(intent.getStringExtra(EXTRA_DATA));
                default:

            }
        }

    }

    private void handleActionDownloadTrip(String tripId) {

    }

    private void handleActionGetTrips(String email) {
        BCApiService apiService = BCApiService.getInstance();
        try {
            List<BCTripResponse> tripResponses = apiService.doLoadTripsByUser(email);
            EventBus.getDefault().post(new BCLoadTripsSuccessEvent(TripUtils.mapAll(tripResponses)));
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BCLoadTripsFailedEvent());
        }
    }

    public class BCDownloadTripSuccessEvent {
        public final BCTripResponse tripResponse;

        public BCDownloadTripSuccessEvent(BCTripResponse tripResponse) {
            this.tripResponse = tripResponse;
        }
    }

    public class BCDownloadTripFailedEvent {

    }

    public class BCLoadTripsSuccessEvent {
        public List<BCTripInfo> data;

        public BCLoadTripsSuccessEvent(List<BCTripInfo> trips) {
            this.data = trips;
        }
    }

    public class BCLoadTripsFailedEvent {

    }
}

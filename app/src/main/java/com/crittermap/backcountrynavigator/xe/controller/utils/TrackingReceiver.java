package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;

import org.greenrobot.eventbus.EventBus;

import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.PAUSE;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.STOP;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.TRACKING;

public class TrackingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("NotificationOnClick", action);
        if (TextUtils.isEmpty(action)) return;
        switch (action) {
            case BCConstant.ACTION_PLAY_TRACKING:
                EventBus.getDefault().post(new BCTrackingStatusChangedEvent(TRACKING));
                BCUtils.saveTrackingStatus(TRACKING);
                BC_TrackingNotification.notify(context, TRACKING);
                break;
            case BCConstant.ACTION_PAUSE_TRACKING:
                EventBus.getDefault().post(new BCTrackingStatusChangedEvent(PAUSE));
                BCUtils.saveTrackingStatus(PAUSE);
                BC_TrackingNotification.notify(context, PAUSE);
                break;
            case BCConstant.ACTION_STOP_TRACKING:
                EventBus.getDefault().post(new BCTrackingStatusChangedEvent(STOP));
                BCUtils.saveTrackingStatus(STOP);

                Intent resultIntent = new Intent(context, BCHomeActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resultIntent.putExtra(BCConstant.NOTIFICATION_KEY, BCConstant.NOTI_STOP_TRACKING);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    BC_TrackingNotification.cancel(context);
                    contentIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

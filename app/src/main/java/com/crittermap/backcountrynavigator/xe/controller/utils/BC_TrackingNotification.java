package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;

import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.TRACKING;

public class BC_TrackingNotification {

    private static final String NOTIFICATION_TAG = "BC_TrackingNotification";
    private static PendingIntent contentIntent;

    public static void notify(final Context context, TrackingStatus status) {
        final Resources res = context.getResources();
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_logo);
        String CHANNEL_ID = "tracking_channel";

        if (contentIntent == null) {
            Intent resultIntent = new Intent(context, BCHomeActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(
                    context,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = "tracking_channel";
        String description = "channel_description_02";
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            mChannel.setDescription(description);
            mChannel.enableVibration(false);
            if (nm != null) {
                nm.createNotificationChannel(mChannel);
            }
        }

        Intent trackingIntent = new Intent(context, TrackingReceiver.class);
        trackingIntent.putExtra(BCConstant.ACTION, BCConstant.ACTION_PLAY_TRACKING);
        trackingIntent.setAction(BCConstant.ACTION_PLAY_TRACKING);
        PendingIntent trackingPendingIntent = PendingIntent.getBroadcast(context, 0, trackingIntent, 0);

        Intent pauseIntent = new Intent(context, TrackingReceiver.class);
        pauseIntent.putExtra(BCConstant.ACTION, BCConstant.ACTION_PAUSE_TRACKING);
        pauseIntent.setAction(BCConstant.ACTION_PAUSE_TRACKING);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, 0);

        Intent stopIntent = new Intent(context, TrackingReceiver.class);
        stopIntent.putExtra(BCConstant.ACTION, BCConstant.ACTION_STOP_TRACKING);
        stopIntent.setAction(BCConstant.ACTION_STOP_TRACKING);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                // Set ticker text (preview) information for this notification.
                .setContentTitle("BCNAVXE - Recording track")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(picture)
                .setContentIntent(contentIntent)
                .setAutoCancel(false);
        if (status == TRACKING) {
            builder.addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Now recording a new track")
                            .setBigContentTitle("BCNAVXE - Recording track"))
                    .setContentText("Now recording a new track");
        } else {
            builder.addAction(R.drawable.ic_resume, "Resume", trackingPendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Paused recording your track")
                            .setBigContentTitle("BCNAVXE - Recording track"))
                    .setContentText("Paused recording your track");
        }

        builder.addAction(R.drawable.ic_recording, "Stop", stopPendingIntent);
        notify(context, builder.build());
    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.notify(NOTIFICATION_TAG, 0, notification);
            } else {
                nm.notify(NOTIFICATION_TAG.hashCode(), notification);
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.cancel(NOTIFICATION_TAG, 0);
            } else {
                nm.cancel(NOTIFICATION_TAG.hashCode());
            }
        }
    }
}



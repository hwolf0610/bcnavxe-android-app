package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;

/**
 * Helper class for showing and canceling bc  tiles download
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class BC_TilesDownloadNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "BC_TilesDownload";
    private static NotificationOnClickReceiver onClickReceiver;
    private static PendingIntent pendingCancelIntent;
    private static PendingIntent contentIntent;

    public static final int BC_TILES_DOWNLOAD_NOTIFICATION_ID = 0x1;

    public static void notify(final Context context, final int number, final int current, final int total, final int fail) {
        Notification notif = create(context, number, current, total, fail);
        notify(context, notif);

    }

    public static Notification create(final Context context, final int number, final int current, final int total, final int fail) {
        final Resources res = context.getResources();
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
        String CHANNEL_ID = "bcnavxe_channel_01";
        String NOTIFICATION_FLAG = "SENT_SMS";

        @SuppressLint("DefaultLocale") final String ticker = String.format("%d on %d", current, total);
        final String title = res.getString(
                R.string.bc__tiles_download_notification_title_template, ticker);
        StringBuilder stringBuilder = new StringBuilder();
        if (current == total || current + fail == total) {
            stringBuilder.append("All downloads complete");
        } else {
            stringBuilder.append(res.getString(R.string.bc__tiles_download_notification_placeholder_text_template));
        }
        stringBuilder.append("\n");
        stringBuilder.append("Fails:");
        stringBuilder.append(fail);
        String text = stringBuilder.toString();

        if (onClickReceiver == null) {
            onClickReceiver = new NotificationOnClickReceiver();
            context.registerReceiver(onClickReceiver, new IntentFilter(NOTIFICATION_FLAG));
            Intent intent = new Intent(NOTIFICATION_FLAG);
            intent.putExtra("ACTION", "STOP_DOWNLOAD");
            pendingCancelIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

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
        CharSequence name = "channel_bcnavxe";
        String description = "channel_description";
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableVibration(false);
            if (nm != null) {
                nm.createNotificationChannel(mChannel);
            }
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_stat_bc__tiles_download)
                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)
                .setOngoing(true)
                .setContentTitle(title)
                .setContentText(text)
                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)
                .setProgress(total, current, false)
                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(contentIntent)
                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText("Download Tiles"))

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);
        if (current != total) {
            builder.addAction(
                    R.drawable.ic_cancel_black_24dp,
                    res.getString(R.string.action_cancel),
                    pendingCancelIntent);
        }
        Notification notif = builder.build();
        return notif;

    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.notify(NOTIFICATION_TAG, BC_TILES_DOWNLOAD_NOTIFICATION_ID, notification);
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

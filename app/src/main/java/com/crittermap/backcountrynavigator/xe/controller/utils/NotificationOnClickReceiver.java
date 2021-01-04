package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_EventNotification;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by nhat@saveondev.com on 1/14/18.
 */

public class NotificationOnClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().postSticky(new BC_EventNotification(intent.getStringExtra("ACTION")));
    }
}

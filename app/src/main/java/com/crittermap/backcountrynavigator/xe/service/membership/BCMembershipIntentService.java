package com.crittermap.backcountrynavigator.xe.service.membership;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.eventbus.BCGetMembershipSuccessEvent;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class BCMembershipIntentService extends IntentService {
    private static final String ACTION_GET_MEMBERSHIP = "com.crittermap.backcountrynavigator.xe.service.action.GET_MEMBERSHIP";
    private static final String EXTRA_USERNAME = "com.crittermap.backcountrynavigator.xe.service.action.USERNAME";

    public BCMembershipIntentService() {
        super(BCMembershipIntentService.class.getSimpleName());
    }

    public static void startActionGetMembership(Context context, String userName) {
        if (TextUtils.isEmpty(userName)) return;
        Intent intent = new Intent(context, BCMembershipIntentService.class);
        intent.setAction(ACTION_GET_MEMBERSHIP);
        intent.putExtra(EXTRA_USERNAME, userName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            assert action != null;
            switch (action) {
                case ACTION_GET_MEMBERSHIP:
                    handleActionGetMembership(intent.getStringExtra(EXTRA_USERNAME));
                    break;
                default:

            }
        }

    }

    private void handleActionGetMembership(String userId) {
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCMembership membership = apiService.doGetMembership(userId);
            EventBus.getDefault().post(new BCGetMembershipSuccessEvent(membership));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

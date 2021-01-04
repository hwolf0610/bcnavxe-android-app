package com.crittermap.backcountrynavigator.xe.singleton;

import android.content.Context;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;

/**
 * Created by nhatdear on 3/11/18.
 */

public class BCSingleton {
    private static final BCSingleton ourInstance = new BCSingleton();

    private Context context;

    public static BCSingleton getInstance() {
        return ourInstance;
    }

    private BCUser currentUser;

    private boolean isUsingOfflineMode = false;

    private BCSingleton() {
    }

    public BCUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(BCUser currentUser) {
        this.currentUser = currentUser;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isUsingOfflineMode() {
        return isUsingOfflineMode;
    }

    public void setUsingOfflineMode(boolean usingOfflineMode) {
        isUsingOfflineMode = usingOfflineMode;
    }
}

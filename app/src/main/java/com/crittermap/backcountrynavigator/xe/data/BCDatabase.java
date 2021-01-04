package com.crittermap.backcountrynavigator.xe.data;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by henry on 3/14/2018.
 */

@Database(name = BCDatabase.NAME, version = BCDatabase.VERSION)
public class BCDatabase {
    static final String NAME = "bcnavxedb";
    static final int VERSION = 1803251526;

    // Clear all data in database
    public static void clearAllData() {
        FlowManager.getDatabase(BCDatabase.class).reset(BCApp.getInstance());
        FlowManager.destroy();
        FlowManager.init(new FlowConfig.Builder(BCApp.getInstance()).build());
    }

}

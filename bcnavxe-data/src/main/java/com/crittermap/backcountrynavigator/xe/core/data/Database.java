package com.crittermap.backcountrynavigator.xe.core.data;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

@com.raizlabs.android.dbflow.annotation.Database(name = Database.NAME, version = Database.VERSION)
public class Database {
    static final String NAME = "bcnavxedb_data";
    static final int VERSION = 1803251526;

    // Clear all data in database
    public static void clearAllData(Context context) {
        FlowManager.getDatabase(Database.class).reset(context);
        FlowManager.destroy();
        FlowManager.init(new FlowConfig.Builder(context)
                .build());
    }
}

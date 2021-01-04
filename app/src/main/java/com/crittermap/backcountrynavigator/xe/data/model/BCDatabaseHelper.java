package com.crittermap.backcountrynavigator.xe.data.model;

import com.raizlabs.android.dbflow.structure.BaseModel;


public class BCDatabaseHelper {

    public static <T extends BaseModel> void save(T object) {
        if (object != null) {
            object.save();
        }
    }

    public static <T extends BaseModel> void delete(T object) {
        if (object != null) {
            object.delete();
        }
    }

    public static <T extends BaseModel> void update(T object) {
        if (object != null) {
            object.update();
        }
    }
}

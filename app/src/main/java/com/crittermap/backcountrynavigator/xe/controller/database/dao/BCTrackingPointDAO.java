package com.crittermap.backcountrynavigator.xe.controller.database.dao;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrackingPoint;

import static com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper.TABLE_TRACKING_POINT;

public class BCTrackingPointDAO extends CommonDAO<BCTrackingPoint> {

    public BCTrackingPointDAO(String dbName) {
        super(BC_TripsDBHelper.createInstance(dbName),
                TABLE_TRACKING_POINT, "id", BCTrackingPoint.class);
    }
}

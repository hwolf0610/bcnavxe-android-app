package com.crittermap.backcountrynavigator.xe.controller.database.dao;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;

import static com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper.TABLE_TRIP;


public class BCTripDAO extends CommonDAO<BCTrip> {

    public BCTripDAO(String dbName) {
        super(BC_TripsDBHelper.createInstance(dbName),
                TABLE_TRIP, "id", BCTrip.class);
    }

    public BCTripDAO(String dbPath, String dbName) {
        super(BC_TripsDBHelper.createInstance(dbPath, dbName),
                TABLE_TRIP, "id", BCTrip.class);
    }
}

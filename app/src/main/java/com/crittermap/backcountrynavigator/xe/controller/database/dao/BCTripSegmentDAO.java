package com.crittermap.backcountrynavigator.xe.controller.database.dao;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripSegment;

import static com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper.TABLE_TRIP_SEGMENT;


public class BCTripSegmentDAO extends CommonDAO<BCTripSegment> {

    public BCTripSegmentDAO(String dbName) {
        super(BC_TripsDBHelper.createInstance(dbName)
                , TABLE_TRIP_SEGMENT, "id", BCTripSegment.class);
    }
}

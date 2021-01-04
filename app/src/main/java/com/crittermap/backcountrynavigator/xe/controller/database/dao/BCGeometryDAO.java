package com.crittermap.backcountrynavigator.xe.controller.database.dao;

import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;

import java.util.List;

import static com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper.TABLE_GEOMETRY;


public class BCGeometryDAO extends CommonDAO<BCGeometry> {
    public BCGeometryDAO(String dbName) {
        super(BC_TripsDBHelper.createInstance(dbName),
                TABLE_GEOMETRY, "id", BCGeometry.class);
    }

    public BCGeometryDAO(String dbPath, String dbName) {
        super(BC_TripsDBHelper.createInstance(dbPath, dbName),
                TABLE_GEOMETRY, "id", BCGeometry.class);
    }

    @Override
    public String insertOrUpdate(BCGeometry object) {
        String type = TextUtils.isEmpty(object.getType()) ? null : object.getType().toLowerCase();
        object.setType(type);
        return super.insertOrUpdate(object);
    }

    public void insertOrUpdate(List<BCGeometry> geometries) {
        for (BCGeometry geometry : geometries) {
            insertOrUpdate(geometry);
        }
    }

    public List<BCGeometry> getNonTrackingGeometries() throws IllegalAccessException {
        return retrieve(null, "desc is NULL", null,null,null,null,null);
    }
}

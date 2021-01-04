package com.crittermap.backcountrynavigator.xe.controller.database;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripSegmentDAO;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripSegment;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripColor;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripFeature;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.esri.arcgisruntime.geometry.Geometry;
import com.raizlabs.android.dbflow.data.Blob;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.DB_TRACKING_TEMP;

/**
 * Created by thutrang.dao on 3/26/18.
 */

public class BC_TripsDBHelper extends BC_AbstractDatabaseHelper {

    public static final String TABLE_TRIP_SEGMENT = "tripSegment";
    public static final String TABLE_TRIP = "trip";
    public static final String TABLE_TRACKING_POINT = "trackingPoint";
    public static final String TABLE_GEOMETRY = "geometry";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TRIP_ID = "tripId";
    private static final String COLUMN_START_LOC = "startLoc";
    private static final String COLUMN_END_LOC = "endLoc";
    private static final String COLUMN_MIN_LEVEL = "minLevel";
    private static final String COLUMN_MAX_LEVEL = "maxLevel";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESC = "desc";
    private static final String COLUMN_LONG_DESC = "longDesc";
    private static final String COLUMN_UNICODE_DESC = "unicodeDesc";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    protected BC_TripsDBHelper(String dbName) {
        super(BC_Helper.getTripDBPath(dbName), dbName, null);
    }

    protected BC_TripsDBHelper(String dbPath, String dbName) {
        super(dbPath, dbName, null);
    }

    static protected ConcurrentHashMap<String,BC_TripsDBHelper> instanceMap = new ConcurrentHashMap<String,BC_TripsDBHelper>();


    synchronized static public BC_TripsDBHelper createInstance(String dbPath, String dbName)
    {

        if(instanceMap.contains(dbPath))
        {
            return instanceMap.get(dbPath);
        }
        else
        {

            BC_TripsDBHelper helper = new BC_TripsDBHelper(dbPath, dbName);
            instanceMap.put(dbPath,helper);
            return helper;
        }
    }

    static public BC_TripsDBHelper createInstance(String dbName)
    {
        return createInstance(BC_Helper.getTripDBPath(dbName), dbName);

    }



    public static void saveTripResponse(BCTripResponse response) {
        saveTrip(response);
        String segmentId = saveTripSegment(response);
        saveTripGeometry(response, segmentId);
    }

    public static void updateTrip(BCTripResponse response) throws IllegalAccessException {
        BCTrip trip = saveTrip(response);
        String segmentId = getSegmentId(trip);
        saveTripGeometry(response, segmentId);
    }


    public static void saveLocalTrip(BCTrip trip, List<BCGeometry> geometries) throws IllegalAccessException {
        saveLocalTrip(trip, geometries, true);
    }

    public static void saveLocalTrip(BCTrip trip, List<BCGeometry> geometries, boolean isCreateNewSegment) throws IllegalAccessException {

        if (TextUtils.isEmpty(trip.getId())) {
            trip.setId(UUID.randomUUID().toString());
        }

        new BCTripDAO(trip.getId()).insertOrUpdate(trip);

        String segmentId;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (isCreateNewSegment) {
            segmentId = saveTripSegment(trip);

            BCTripInfo bcTripInfo = new BCTripInfo();
            bcTripInfo.setId(trip.getId());
            bcTripInfo.setName(trip.getName());
            bcTripInfo.setOwnerId(trip.getOwnerId());
            bcTripInfo.setTripStatus(TripStatus.NOT_UPLOADED);
            bcTripInfo.setTrekFolder(trip.getFolder());
            bcTripInfo.setImage(new Blob(trip.getImage()));
            bcTripInfo.setTimestamp(timestamp.getTime());
            BCTripInfoDBHelper.save(bcTripInfo);
        } else {
            segmentId = getSegmentId(trip);

            BCTripInfo bcTripInfo = BCTripInfoDBHelper.get(trip.getId());
            bcTripInfo.setTimestamp(timestamp.getTime());
            BCTripInfoDBHelper.save(bcTripInfo);
        }

        if (geometries != null && geometries.size() > 0) {
            BCGeometryDAO bcGeometryDAO = new BCGeometryDAO(trip.getId());
            for (BCGeometry geometry : geometries) {
                geometry.setTripSegmentId(segmentId);
                geometry.setTripId(trip.getId());
                bcGeometryDAO.insertOrUpdate(geometry);
            }
        }
    }

    private static String getSegmentId(BCTrip trip) throws IllegalAccessException {
        String segmentId;
        List<BCTripSegment> l = new BCTripSegmentDAO(trip.getId()).getAll();
        if (l.size() > 0) {
            segmentId = l.get(0).getId();
        } else {
            segmentId = saveTripSegment(trip);
        }
        return segmentId;
    }

    private static void saveTripGeometry(BCTripResponse response, String segmentId) {
        BCGeometryDAO geometryDAO = new BCGeometryDAO(response.getId());
        geometryDAO.deleteAll();

        for (BCTripFeature feature : response.getFeatures()) {
            if (feature.getGeometry() != null) {
                BCGeometry geometry = new BCGeometry();
                geometry.setTripId(response.getId());
                geometry.setTripSegmentId(segmentId);

                geometry.setType(feature.getGeometry().getType());
                geometry.setGeoJSON(((Geometry) feature.getGeometry().getCoordinates()).toJson());

                BCTripColor color = feature.getProperties().getColor();

                geometry.setColor(Color.argb((int) (color.a * 100), color.r, color.g, color.b));
                geometry.setWidth(feature.getProperties().getWidth());

                geometryDAO.insertOrUpdate(geometry);
            }
        }
    }

    private static String saveTripSegment(BCTrip bcTrip) {
        BCTripSegment tripSegment = new BCTripSegment();
        tripSegment.setTripId(bcTrip.getId());
        tripSegment.setName(bcTrip.getName());
        return new BCTripSegmentDAO(bcTrip.getId()).insertOrUpdate(tripSegment);
    }

    private static String saveTripSegment(BCTripResponse response) {
        BCTripSegment tripSegment = new BCTripSegment();
        tripSegment.setTripId(response.getId());
        tripSegment.setName(response.getName());
        return new BCTripSegmentDAO(response.getId()).insertOrUpdate(tripSegment);
    }

    private static BCTrip saveTrip(BCTripResponse response) {
        BCTrip trip = new BCTrip();
        trip.setId(response.getId());
        trip.setName(response.getName());
        trip.setSharedType(response.getAccess());
        new BCTripDAO(response.getId()).insertOrUpdate(trip);
        return trip;
    }

    @Override
    protected void onCreate(SQLiteDatabase db, metadata metadata) {
        db.beginTransaction();
        String createTripSegmentTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s " +
                        "(%s TEXT PRIMARY KEY,%s TEXT, %s TEXT," +
                        "%s TEXT, %s INTEGER, %s INTEGER, %s TEXT," +
                        "%s TEXT, %s TEXT, %s TEXT, %s INTEGER);"
                , TABLE_TRIP_SEGMENT
                , COLUMN_ID, COLUMN_TRIP_ID, COLUMN_START_LOC
                , COLUMN_END_LOC, COLUMN_MIN_LEVEL, COLUMN_MAX_LEVEL, COLUMN_NAME
                , COLUMN_DESC, COLUMN_LONG_DESC, COLUMN_UNICODE_DESC, COLUMN_TIMESTAMP
        );

        String createTripTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s " +
                        "(%s TEXT PRIMARY KEY,%s TEXT, %s TEXT, %s TEXT," +
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT," +
                        "%s BLOB, %s INTEGER);"
                , TABLE_TRIP
                , "id", "name", "startLoc", "endLoc"
                , "desc", "longDesc", "unicodeDesc", "sharedType"
                , "sharedLink", "ownerId", "membershipType", "timestamp", "folder"
                , "image", "tripZoom"
        );

        String createGeometryTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s " +
                        "(%s TEXT PRIMARY KEY, %s TEXT, %s INTEGER, %s REAL" +
                        ", %s REAL, %s TEXT, %s INTEGER, %s INTEGER, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s INTEGER, " +
                        "%s INTEGER, %s TEXT, %s INTEGER," +
                        "%s TEXT, %s TEXT);"
                , TABLE_GEOMETRY
                , "id", "tripId", "tripSegmentId", "longitude"
                , "latitude", "gTileID", "minLevel", "maxLevel"
                , "type", "name", "desc", "longDesc"
                , "unicodeDesc", "geoJSON", "mediaLink", "timestamp"
                , "color", "imageUrl", "width"
                , "lineStyle", "fillStyle"
        );


        Logger.d("create schema " + getDATABASE_NAME());

        db.execSQL(createTripSegmentTableQuery);
        db.execSQL(createTripTableQuery);
        db.execSQL(createGeometryTableQuery);

        if (DB_TRACKING_TEMP.equals(getDATABASE_NAME())) {
            String createTrackingPointTableQuery = "CREATE TABLE trackingPoint " +
                    "(id TEXT PRIMARY KEY, " +
                    "jsonPoint TEXT, " +
                    "groupId INTEGER);";
            db.execSQL(createTrackingPointTableQuery);
        }

        db.execSQL("CREATE UNIQUE INDEX id ON trip (id)");
        db.execSQL("COMMIT");
    }
}

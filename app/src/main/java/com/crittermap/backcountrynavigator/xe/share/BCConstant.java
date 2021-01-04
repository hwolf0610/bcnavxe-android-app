package com.crittermap.backcountrynavigator.xe.share;

/**
 * Created by henry on 3/8/2018.
 */

public class BCConstant {
    //public static final String API_URL = "https://bcnavxe.com";
    public static final String API_URL = "http://ec2-54-191-29-36.us-west-2.compute.amazonaws.com";
    public static final int TIME_OUT = 120;
    public static String GEOCODER_API = "https://maps.googleapis.com";
    public static String GEOCODER_API_KEY = "AIzaSyDQGThYtJ8DXbD06cTiU2JXK2SrVlG3-Hg";
    // TRIP ACTIVITY
    public static final int TRIP_ACTIVITY = 1;
    public static final String TRIP_ACTION = "TRIP_ACTION";
    public static final int TRIP_ACTION_UPDATE_VALUE = 1;
    // MAP SOURCE ACTIVITY
    public static final int MAP_SOURCE_ACTIVITY = 2;
    public static final String MAP_ACTION = "MAP_ACTION";
    public static final int MAP_ACTION_UPDATE_VALUE = 1;
    // ACCOUNT ACTIVITY
    public static final int REQUEST_PICK_IMAGE = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int IMAGE_WIDTH = 200;
    public static final int IMAGE_HEIGHT = 200;
    public static String TERM_OF_SERVICE_URL = "https://bcnavxe.com";
    public static String PRIVACY_POLICY_URL = "https://bcnavxe.com";
    public static int minLength = 1;
    public static int maxLength = 16;
    public static int minPassword = 6;
    public static int maxPasswordLength = 50;
    // MAP FILTER
    public static final String TOPO_FILTER = "topo";
    public static final String MARINE_FILTER = "marine";
    public static final String HYBRID_FILTER = "hybrid";
    public static final String SATELLITE_FILTER = "satellite";
    public static final String STREET_FILTER = "street";
    public static final String AVIATION_FILTER = "aviation";

    //Notification action
    public static final String ACTION = "ACTION";
    public static final String ACTION_STOP_DOWNLOAD = "STOP_DOWNLOAD";
    public static final String ACTION_PLAY_TRACKING = "com.crittermap.backcountrynavigator.xe.GET_TRIP_FOR_TRACKING";
    public static final String ACTION_PAUSE_TRACKING = "com.crittermap.backcountrynavigator.xe.PAUSE";
    public static final String ACTION_STOP_TRACKING = "com.crittermap.backcountrynavigator.xe.STOP";
    public static final String NOTIFICATION_KEY = "Notification";
    public static final String NOTI_STOP_TRACKING = "STOP_TRACKING";

    //Intent extra
    public static final String INTENT_EXTRA_TRIP_ID = "tripId";
}

package com.crittermap.backcountrynavigator.xe.share;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.BCMembershipDataHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembershipType;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMapDBHelper;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus;
import com.crittermap.backcountrynavigator.xe.singleton.BCSingleton;
import com.crittermap.backcountrynavigator.xe.ui.BCMainActivity;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;
import com.crittermap.backcountrynavigator.xe.ui.login.BCLoginActivity;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.crittermap.backcountrynavigator.xe.controller.constant.BC_CONSTANTS.MAX_FILE_SIZE;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.STOP;

/**
 * Created by nhatdear on 3/10/18.
 */

public class BCUtils {
    private final static String BC_SHARED_PREF = "BCSharedPrefs";
    private final static String KEY_USER_PROFILE = "BCUserProfile";
    public static final String KEY_SHOW_DOWNLOAD_TUTORIAL = "showDownloadMapTutorial";
    private final static String KEY_LAST_MAP = "BCLastMap";
    private final static String KEY_USER_SETTINGS = "BCUserSettings";
    private static final String KEY_TRACKING_STATUS = "BCTrackingStatus";
    private static final String KEY_LAST_EDITED_TRIP_ID = "BCTrackingTripId";
    public static final String SHARED_TRIP = "SharedTrip";
    private final static String KEY_LAST_LOCATION = "BCLastLocation";
    public static final String DECIMAL_PATTERN = "#,##0.00";
    private final static String KEY_USER_STAT_SETTINGS = "BCUserStatSettings";

    public static void goToHome(Activity activity) {
        Intent intent = new Intent(activity, BCHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getBaseContext().startActivity(intent);
        activity.finish();
    }

    public static void goToHome(Activity activity, String tripId) {
        Intent intent = new Intent(activity, BCHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(SHARED_TRIP, tripId);
        activity.getBaseContext().startActivity(intent);
        activity.finish();
    }

    public static void goToMain(Activity activity) {
        Intent intent = new Intent(activity, BCMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getBaseContext().startActivity(intent);
        activity.finish();
    }

    public static void goToLogin(Activity activity) {
        Intent intent = new Intent(activity, BCLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getBaseContext().startActivity(intent);
        activity.finish();
    }

    public static void saveUserShareRef(BCUser user) {
        Context context = BCSingleton.getInstance().getContext();
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_USER_PROFILE, gson.toJson(user));
        editor.apply();
    }

    public static void clearUserShareRef() {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    public static BCUser getCurrentUser() {
        Context context = BCSingleton.getInstance().getContext();
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        String json = sharedPref.getString(KEY_USER_PROFILE, "");
        return gson.fromJson(json, BCUser.class);
    }

    public static void saveLastMapPref(BCMap map) {
        map.setLastUsedTime(Calendar.getInstance().getTimeInMillis());
        BCMapDBHelper.save(map);

        Context context = BCSingleton.getInstance().getContext();
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LAST_MAP, gson.toJson(map));
        editor.apply();
    }

    public static void clearLastMapPref() {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LAST_MAP, "");
        editor.apply();
    }

    public static BCMap getLastMapPref() {
        Context context = BCSingleton.getInstance().getContext();
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        String json = sharedPref.getString(KEY_LAST_MAP, "");
        return gson.fromJson(json, BCMap.class);
    }

    public static boolean isSharedPrefContainsKey(String key) {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPref.contains(key);
    }

    public static void saveSharedPreferences(String key, boolean value) {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getSharedPrefereneceBoolean(String key) {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public static long parseDate(String date) {
        if (TextUtils.isEmpty(date)) return -1;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String reverseDate(long date) {
        String dateStr= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date(date));
        return dateStr.split("T")[0];
    }

    public static boolean isSameDay(long first, long second) {
        Calendar date1 = Calendar.getInstance();
        date1.setTimeInMillis(first);

        Calendar date2 = Calendar.getInstance();
        date2.setTimeInMillis(second);
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
                && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
                && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conManager != null;
        NetworkInfo i = conManager.getActiveNetworkInfo();
        return !((i == null) || (!i.isConnected()) || (!i.isAvailable()));
    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public static Bitmap loadBitmapWithSize(Uri uri, int width, int height) {
        InputStream is = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            is = BCApp.getInstance().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            is.close();
            options.inSampleSize = calcInSampleSize(options.outWidth, options.outHeight, width, height);
            options.inJustDecodeBounds = false;
            is = BCApp.getInstance().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            return bitmap;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static int calcInSampleSize(int outWidth, int outHeight, int width, int height) {
        int inSampleSize = 1;
        if (outHeight > height || outWidth > width) {
            if (outWidth > outHeight) {
                inSampleSize = (int) (((float) outHeight / (float) height) + 0.5f);
            } else {
                inSampleSize = (int) (((float) outWidth / (float) width) + 0.5f);
            }
        }
        return inSampleSize;
    }

    public static String getRealPathFromURIBelowAPI11(Context context, Uri uri) {
        String filePath = "";
        String wholeID = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            wholeID = DocumentsContract.getDocumentId(uri);
        }

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public static String getRealPathFromURIAPI11to18(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURIAPI19(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static Uri compressImage(Context context, Bitmap inImage, int compressQuality) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, compressQuality, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static String encodeBase64Image(String filePath) {
        try {
            byte[] bytes = imageToBytes(filePath);
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] imageToBytes(String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;

        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            int quality = calculateCompressQuality((int) (imgFile.length() / 1024));
            Bitmap bitmapFromPath = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (filePath.endsWith(".png") || filePath.endsWith(".PNG")) {
                bitmapFromPath.compress(Bitmap.CompressFormat.PNG, quality, bytes);
            } else {
                bitmapFromPath.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
            }


            return bytes.toByteArray();
        }
        return null;
    }


    public static Bitmap replaceTransparentColor(Bitmap bitmap, int color) {
        Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                if (copy.getPixel(x, y) == Color.TRANSPARENT) {
                    copy.setPixel(x, y, color);
                }
            }
        }
        return copy;
    }

    /**
     * @param fileSize in KB
     * @return quality (percentage)
     */
    private static int calculateCompressQuality(int fileSize) {
        if (fileSize < MAX_FILE_SIZE) {
            return 100;
        }

        int qualityPercentage = MAX_FILE_SIZE * 100 / fileSize;
        return (qualityPercentage > 1) ? qualityPercentage : 1;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void clearUserSettingShareRef() {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(KEY_USER_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(KEY_USER_SETTINGS);
        editor.apply();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean isTrackingServiceRunning(Class<?> serviceClass, Context activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void saveTrackingStatus(TrackingStatus status) {
        SharedPreferences sharedPref = BCApp.getInstance().getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_TRACKING_STATUS, status.ordinal());
        editor.apply();
    }

    public static TrackingStatus getTrackingStatus() {
        SharedPreferences sharedPref = BCApp.getInstance().getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        int key = sharedPref.getInt(KEY_TRACKING_STATUS, STOP.ordinal());
        return TrackingStatus.values()[key];
    }

    public static void saveTrackingTrip(String tripId) {
        SharedPreferences sharedPref = BCApp.getInstance().getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LAST_EDITED_TRIP_ID, tripId);
        editor.apply();
    }

    public static String getLastEditedTrip() {
        SharedPreferences sharedPref = BCApp.getInstance().getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_LAST_EDITED_TRIP_ID, "");
    }

    public static void sortMapSource(List<BCMap> mapSources, String state, String country) {
        Collections.sort(mapSources, new BCMapSourceComparator(state, country));
    }

    public static void saveLastLocation(MapView mMapView) {
        Context context = BCSingleton.getInstance().getContext();
        Polygon mapArea = mMapView.getVisibleArea();
        if (mapArea != null) {
            Envelope envelope = mMapView.getVisibleArea().getExtent();
            SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(KEY_LAST_LOCATION, envelope.toJson());
            editor.apply();
        }
    }

    public static Envelope getLastLocation() {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        String json = sharedPref.getString(KEY_LAST_LOCATION, "");
        if (StringUtils.isNotNullOrEmpty(json)) {
            return (Envelope) Geometry.fromJson(json);
        } else {
            return null;
        }
    }

    public static void clearLastLocation() {
        Context context = BCSingleton.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LAST_LOCATION, "");
        editor.apply();
    }

    public static boolean isPurchasedUser() {
        BCUser currentUser = BCUtils.getCurrentUser();
        if (currentUser != null) {
            BCMembership currentMembership = BCMembershipDataHelper.findbyUserId(currentUser.getUserName());
            return currentMembership != null
                    && BCMembershipType.getFromName(currentMembership.getMembershipType()) != null;
        }
        return false;
    }

    public static void getMapPointElevation(final WebServiceCallBack<Double> webServiceCallBack, final Point mapPoint, Context mContext) {
        ArcGISTiledElevationSource standaloneElevationSource = new ArcGISTiledElevationSource(
                mContext.getString(R.string.elevation_image_service));

        standaloneElevationSource.loadAsync();

        final Surface surface = new Surface();

        surface.getElevationSources().add(standaloneElevationSource);

        standaloneElevationSource.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                try {
                    Double elevation = surface.getElevationAsync(mapPoint).get();
                    webServiceCallBack.onSuccess(elevation);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    webServiceCallBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //FIXME centralize data
    public static BCUserStatistic getUserStatSettings() {
        Gson gson = new Gson();
        SharedPreferences sharedPref = BCApp.getInstance().getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        BCUserStatistic statistic = gson.fromJson(sharedPref.getString(KEY_USER_STAT_SETTINGS, ""), BCUserStatistic.class);
        if (statistic == null || statistic.getUserStatsList() == null || statistic.getUserStatsList().size() <= 0) {
            statistic = new BCUserStatistic();
            saveUserStatSettings(statistic);
        }
        return statistic;
    }

    //FIXME centralize data
    public static void saveUserStatSettings(BCUserStatistic userStats) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = BCApp.getInstance().getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_USER_STAT_SETTINGS, gson.toJson(userStats));
        editor.apply();
    }

    //FIXME centralize data
    public static void resetUserStatisticValue() {
        BCUserStatistic bcUserStatistic = getUserStatSettings();
        bcUserStatistic.setStartTime(0);
        bcUserStatistic.setMovingTime(0);
        bcUserStatistic.setLastSpeed(0);
        bcUserStatistic.setLastAltitudeGain(0);
        bcUserStatistic.setLastDistance(0);

        for (BCStatisticModel model : bcUserStatistic.getUserStatsList()) {
            model.setContent("");
        }
        saveUserStatSettings(bcUserStatistic);
    }

    public static String getBearingString(Point point1, Point point2) {
        point1 = (Point) GeometryEngine.project(point1, SpatialReferences.getWgs84());
        point2 = (Point) GeometryEngine.project(point2, SpatialReferences.getWgs84());
        return getBearingString(point1.getY(), point1.getX(), point2.getY(), point2.getX());
    }

    private static String getBearingString(double lat1, double lon1, double lat2, double lon2) {
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(lon2 - lon1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
        double resultDegree = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
        return getBearingStringFromDegree(resultDegree);
    }

    public static String getBearingStringFromDegree(double resultDegree) {
        String coordNames[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};
        double directionid = Math.round(resultDegree / 22.5);
        // no of array contain 360/16=22.5
        if (directionid < 0) {
            directionid = directionid + 16;
            //no. of contains in array
        }
        String compasLoc = coordNames[(int) directionid];

        return (int) resultDegree + " " + compasLoc;
    }

    public static boolean isTracking() {
        return getTrackingStatus() != STOP;
    }

    public static Point getMapCenterPoint(MapView mapView) {
        float centreX = mapView.getX() + mapView.getWidth() / 2;
        float centreY = mapView.getY() + mapView.getHeight() / 2;

        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(centreX), Math.round(centreY));
        Point mapPoint = mapView.screenToLocation(screenPoint);

        return (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
    }
}

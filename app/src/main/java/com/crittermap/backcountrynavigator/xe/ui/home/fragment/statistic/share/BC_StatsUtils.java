package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.share;

import android.location.Location;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.DateTimeUtils;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.crittermap.backcountrynavigator.xe.share.BCUtils.DECIMAL_PATTERN;
import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.share.BC_UnitUtils.meterToFeet;

public class BC_StatsUtils {
    private static final String SPACE = " ";
    private static final String KILOMETERS_PER_HOURS = "km/h";
    private static final String MILES_PER_HOURS = "mi/h";
    private static final String KILOMETERS = "km";
    private static final String MILES = "miles";
    private static final String HEC = "ha";
    private static final String ACR = "acr";
    private static final String TAG = BC_StatsUtils.class.getSimpleName();

    public static void updateStatistics(Polyline polyline, Point previousPoint, Point currentLocation, Location location, BCSettings settings) {
        try {
            Log.d(TAG, "Start update user statistics");
            Log.d(TAG, "Previous point: " + previousPoint.toJson());
            Log.d(TAG, "Current point: " + currentLocation.toJson());
            BCUserStatistic userStats = BCUtils.getUserStatSettings();
            if (userStats.getStartTime() == 0) {
                userStats.setStartTime(Calendar.getInstance().getTimeInMillis());
            }

            DecimalFormat df = new DecimalFormat(DECIMAL_PATTERN);
            previousPoint = (Point) GeometryEngine.project(previousPoint, SpatialReferences.getWebMercator());
            currentLocation = (Point) GeometryEngine.project(currentLocation, SpatialReferences.getWebMercator());
            polyline = (Polyline) GeometryEngine.project(polyline, SpatialReferences.getWebMercator());
            double distanceInKilometer = GeometryEngine.length(polyline) / 1000;
            double timeInHour = (currentLocation.getM() - previousPoint.getM()) / (3600 * 1000);
            double currentSpeedInKilometer = distanceInKilometer / timeInHour;
            double totalDistanceInKilometer = userStats.getLastDistance() + distanceInKilometer;
            //HACK, prevent 0
            double totalTimeInMiliseconds = (double) (Calendar.getInstance().getTimeInMillis() + 1 - userStats.getStartTime());

            Log.d(TAG, "distanceInKilometer: " + distanceInKilometer);
            Log.d(TAG, "timeInHour: " + timeInHour);
            Log.d(TAG, "currentSpeedInKilometer: " + currentSpeedInKilometer);
            Log.d(TAG, "totalDistanceInKilometer: " + totalDistanceInKilometer);
            Log.d(TAG, "totalTimeInMiliseconds: " + totalTimeInMiliseconds);

            for (BCStatisticModel model : userStats.getUserStatsList()) {
                if (model.isShowOnMinimize() || model.isShowOnFull()) {
                    switch (model.getStat()) {
                        case SPEED:
                            model.setContent(buildSpeedStringBySettings(currentSpeedInKilometer, settings));
                            break;
                        case HEADING:
                            if (location.hasBearing()) {
                                model.setContent(BCUtils.getBearingStringFromDegree(location.getBearing()));
                            } else {
                                model.setContent("0");
                            }
                            break;
                        case ACCURACY:
                            if (location.hasAccuracy()) {
                                model.setContent(buildAccuracyStringBySettings(location.getAccuracy(), settings));
                            } else {
                                model.setContent("0");
                            }
                            break;
                        case ATTITUDE:
                            model.setContent(buildAltitudeStringBySettings(currentLocation.getZ(), settings));
                            break;
                        case ATTITUDE_GAIN_LOSS:
                            model.setContent(buildAltitudeGainLossStringBySettings(location.getAltitude(), userStats, settings));
                            break;
                        case AVG_SPEED:
                            double avgSpeedInKilometer = totalDistanceInKilometer * (3600 * 1000) / totalTimeInMiliseconds;
                            model.setContent(buildSpeedStringBySettings(avgSpeedInKilometer, settings));
                            break;
                        case MOVING_TIME:
                            if (currentSpeedInKilometer > 0) {
                                userStats.setMovingTime((long) (userStats.getMovingTime() + (currentLocation.getM() - previousPoint.getM())));
                            }
                            Date zero = Calendar.getInstance().getTime();
                            zero.setTime(0);
                            Date now = Calendar.getInstance().getTime();
                            now.setTime(userStats.getMovingTime());
                            model.setContent(DateTimeUtils.printDifference(zero, now));
                            break;
                        case MAX_SPEED:
                            if (userStats.getLastSpeed() < currentSpeedInKilometer) {
                                model.setContent(buildSpeedStringBySettings(currentSpeedInKilometer, settings));
                            }
                            break;
                        case MAX_ATTITUDE:
                            if (userStats.getLastAltitude() <= currentLocation.getZ()) {
                                model.setContent(df.format(currentLocation.getZ()));
                            }
                            break;
                        case MIN_ATTITUDE:
                            if (userStats.getLastAltitude() >= currentLocation.getZ()) {
                                model.setContent(buildAltitudeStringBySettings(currentLocation.getZ(), settings));
                            }
                            break;
                    }
                }
            }

            userStats.setLastLocation(buildLocationStringBySettings(currentLocation, settings));
            userStats.setLastDistance(totalDistanceInKilometer);
            userStats.setLastAltitude(location.getAltitude());
            userStats.setLastSpeed(currentSpeedInKilometer);

            BCUtils.saveUserStatSettings(userStats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildAltitudeGainLossStringBySettings(double locationAltitude, final BCUserStatistic userStats, final BCSettings settings) {
        return new GainLossAltitude(
                userStats.getLastAltitudeGain(),
                userStats.getLastAltitudeLoss(),
                userStats.getLastAltitude(),
                locationAltitude,
                settings,
                userStats).toString();
    }


    private static String buildAltitudeStringBySettings(double attitudeInMeters, BCSettings settings) {
        StringBuilder builder = new StringBuilder();
        if (settings.getDistance() == BCSettings.DISTANCE.Miles) {
            String intValueAsString = String.valueOf((int) meterToFeet(attitudeInMeters));
            builder.append(intValueAsString);
            builder.append(" feet");
        } else {
            String intValueAsString = String.valueOf((int) attitudeInMeters);
            builder.append(intValueAsString);
            builder.append(" meters");
        }

        return builder.toString();
    }

    private static String buildAccuracyStringBySettings(double accuracyInMeters, BCSettings settings) {
        StringBuilder builder = new StringBuilder();

        if (settings.getDistance() == BCSettings.DISTANCE.Miles) {
            String intValueAsString = String.valueOf((int) meterToFeet(accuracyInMeters));
            builder.append(intValueAsString);
            builder.append(" feet");
        } else {
            String intValueAsString = String.valueOf((int) accuracyInMeters);
            builder.append(intValueAsString);
            builder.append(" meters");
        }

        return builder.toString();
    }

    private static String buildSpeedStringBySettings(double speedInKilometer, BCSettings settings) {
        StringBuilder builder = new StringBuilder();
        switch (settings.getDistance()) {
            case Km:
                builder.append(String.format(Locale.US, "%.2f", speedInKilometer))
                        .append(SPACE)
                        .append(KILOMETERS_PER_HOURS);
                break;
            case Miles:
                builder.append(String.format(Locale.US, "%.2f", BC_UnitUtils.kilometersToMiles(speedInKilometer)))
                        .append(SPACE)
                        .append(MILES_PER_HOURS);
        }
        return builder.toString();
    }

    public static String buildDistanceStringBySettings(double distanceInKilometer, BCSettings settings) {
        StringBuilder builder = new StringBuilder();
        switch (settings.getDistance()) {
            case Km:
                builder.append(String.format(Locale.US, "%.2f", distanceInKilometer))
                        .append(SPACE)
                        .append(KILOMETERS);
                break;
            case Miles:
                builder.append(String.format(Locale.US, "%.2f", BC_UnitUtils.kilometersToMiles(distanceInKilometer)))
                        .append(SPACE)
                        .append(MILES);
        }
        return builder.toString();
    }

    public static String buildAreaStringBySettings(double polygonAreaInKilometer, BCSettings settings) {
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat(DECIMAL_PATTERN);
        switch (settings.getArea()) {
            case Hec:
                builder.append(df.format(BC_UnitUtils
                        .kilometerToHec(polygonAreaInKilometer)))
                        .append(SPACE)
                        .append(HEC);
                break;
            case Acr:
                builder.append(df.format(BC_UnitUtils
                        .kilometerToAcr(polygonAreaInKilometer)))
                        .append(SPACE)
                        .append(ACR);
        }
        return builder.toString();
    }

    public static String buildLocationStringBySettings(Point center, BCSettings settings) {
        StringBuilder builder = new StringBuilder();
        switch (settings.getCoordinates()) {
            case Dec:
                builder.append(BC_UnitUtils.pointToDec(center));
                break;
            case Deg:
                builder.append(BC_UnitUtils.pointToDeg(center));
                break;
            case Dms:
                builder.append(BC_UnitUtils.pointToDms(center));
                break;
            case UTM:
                builder.append(BC_UnitUtils.pointToUTM(center));
                break;
            case MGRS:
                builder.append(BC_UnitUtils.pointToMGRS(center));
                break;
        }
        return builder.toString();
    }

    static class GainLossAltitude {
        double gain;
        double loss;
        Double last;
        double current;
        final BCSettings settings;
        final BCUserStatistic userStatistic;

        GainLossAltitude(double gain, double loss, Double last, double current, final BCSettings settings, final BCUserStatistic statistic) {
            this.gain = gain;
            this.loss = loss;
            this.last = last;
            this.current = current;
            this.settings = settings;
            this.userStatistic = statistic;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            calculateGainLoss();

            if (settings.getDistance() == BCSettings.DISTANCE.Miles) {
                String intValueAsStringGain = String.valueOf((int) meterToFeet(gain));
                String intValueAsStringLoss = String.valueOf((int) meterToFeet(loss));
                builder.append(intValueAsStringGain);
                builder.append(" / ");
                builder.append(intValueAsStringLoss);
            } else {
                String intValueAsStringGain = String.valueOf((int) gain);
                String intValueAsStringLoss = String.valueOf((int) loss);
                builder.append(intValueAsStringGain);
                builder.append(" / ");
                builder.append(intValueAsStringLoss);
            }

            return builder.toString();
        }


        private void calculateGainLoss() {
            if (last == null) {
                gain = 0;
                loss = 0;
            } else {
                if (current >= last) {
                    gain += (current - last);
                } else {
                    loss += (last - current);
                }
            }
            userStatistic.setLastAltitude(current);
            userStatistic.setLastAltitudeGain(gain);
            userStatistic.setLastAltitudeLoss(loss);
        }
    }
}

package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.share;

import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.Point;

class BC_UnitUtils {
    private static final int PRECISION = 0;
    private static final double KILOMETER_VS_MILES_RATIO = 0.621;
    private static final double KILOMETER_VS_ACR_RATIO = 247.11;
    private static final double METERS_VS_FEET_RATIO = 0.3048;
    private static int DECIMAL_PLACE = 2;

    static double kilometersToMiles(double km) {
        return KILOMETER_VS_MILES_RATIO * km;
    }

    static double kilometerToAcr(double km) {
        return km * KILOMETER_VS_ACR_RATIO;
    }

    static double kilometerToHec(double km) {
        return km * 100;
    }

    static double meterToFeet(double m) {
        return m / METERS_VS_FEET_RATIO;
    }

    static double feetToMeter(double feet) {
        return feet * METERS_VS_FEET_RATIO;
    }

    static String pointToDec(Point point) {
        return CoordinateFormatter
                .toLatitudeLongitude(
                        point,
                        CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES,
                        DECIMAL_PLACE);
    }

    static String pointToDeg(Point point) {
        return CoordinateFormatter
                .toLatitudeLongitude(
                        point,
                        CoordinateFormatter.LatitudeLongitudeFormat.DEGREES_DECIMAL_MINUTES,
                        DECIMAL_PLACE);
    }

    static String pointToDms(Point point) {
        return CoordinateFormatter
                .toLatitudeLongitude(
                        point,
                        CoordinateFormatter.LatitudeLongitudeFormat.DEGREES_MINUTES_SECONDS,
                        DECIMAL_PLACE);
    }

    static String pointToUTM(Point point) {
        return CoordinateFormatter
                .toUtm(
                        point,
                        CoordinateFormatter.UtmConversionMode.LATITUDE_BAND_INDICATORS,
                        true);
    }

    static String pointToMGRS(Point point) {
        return CoordinateFormatter
                .toMgrs(
                        point,
                        CoordinateFormatter.MgrsConversionMode.AUTOMATIC,
                        PRECISION,
                        true);
    }
}

package com.crittermap.backcountrynavigator.xe.share;

import android.annotation.SuppressLint;

import java.util.Date;

public class DateTimeUtils {
    @SuppressLint("DefaultLocale")
    public static String printDifference(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        if (elapsedDays == 0) {
            return String.format(
                    "%dh %dm %ds",
                    elapsedHours, elapsedMinutes, elapsedSeconds);
        } else {
            return String.format(
                    "%d days, %dh %dm %ds",
                    elapsedDays,
                    elapsedHours, elapsedMinutes, elapsedSeconds);
        }
    }
}

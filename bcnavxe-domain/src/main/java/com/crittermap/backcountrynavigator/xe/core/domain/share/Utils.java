package com.crittermap.backcountrynavigator.xe.core.domain.share;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public static Boolean savePrefWithKey(String sharedPrefKey, String key, String data, Context context) {
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, data);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

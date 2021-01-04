package com.crittermap.backcountrynavigator.xe.share;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by henry on 3/11/2018.
 */

public class BCCheckValidation {

    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static boolean isWordValid(String word) {
        String wordPattern = "[a-zA-Z0-9 ]*";
        return word.matches(wordPattern) && !word.isEmpty();
    }
}

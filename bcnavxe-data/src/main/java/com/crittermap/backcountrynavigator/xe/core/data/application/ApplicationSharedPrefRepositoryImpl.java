package com.crittermap.backcountrynavigator.xe.core.data.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.crittermap.backcountrynavigator.xe.core.api.dto.Application;
import com.crittermap.backcountrynavigator.xe.core.data.ApplicationSharedPrefRepository;
import com.crittermap.backcountrynavigator.xe.core.domain.share.Utils;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Named;

import static com.crittermap.backcountrynavigator.xe.core.domain.share.SharedKey.BC_SHARED_PREF;

public class ApplicationSharedPrefRepositoryImpl implements ApplicationSharedPrefRepository {
    Context applicationContext;
    Gson gson;
    private String KEY_APP_VERSION = "KEY_APP_VERSION";

    @Inject
    public ApplicationSharedPrefRepositoryImpl(@Named("applicationContext") Context applicationContext, Gson gson) {
        this.applicationContext = applicationContext;
        this.gson = gson;
    }

    @Override
    public Boolean persist(Application data) {
        return Utils.savePrefWithKey(BC_SHARED_PREF, KEY_APP_VERSION, gson.toJson(data), applicationContext);
    }

    @Override
    public Application get() {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(BC_SHARED_PREF, Context.MODE_PRIVATE);
        if (sharedPref.contains(KEY_APP_VERSION)) {
            String json = sharedPref.getString(KEY_APP_VERSION, "");
            return gson.fromJson(json, Application.class);
        } else {
            return new Application();
        }
    }
}

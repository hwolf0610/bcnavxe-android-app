package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.converter.TypeConverter;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class UserStatsTypeConverter extends TypeConverter<String, UserStats> {

    //FIXME Inject gson
    @Override
    public String getDBValue(UserStats model) {
        Gson gson = new Gson();
        return model == null ? null : gson.toJson(model);
    }

    @Override
    public UserStats getModelValue(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, UserStats.class);
    }
}

package com.crittermap.backcountrynavigator.xe.service.map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by henry on 4/21/2018.
 */

public class BCMapIntentService extends IntentService {

    private static final String ACTION_GET_MAPS = "com.crittermap.backcountrynavigator.xe.service.map.ACTION_GET_MAPS";
    private static final String ACTION_GET_MAPS_LOCAL = "com.crittermap.backcountrynavigator.xe.service.map.ACTION_GET_MAPS_LOCAL";
    private static final String ACTION_GET_DOWNLOADED_MAPS_LOCAL = "com.crittermap.backcountrynavigator.xe.service.map.ACTION_GET_DOWNLOADED_MAPS_LOCAL";
    private static final String ACTION_FAVORITE_MAPS = "com.crittermap.backcountrynavigator.xe.service.map.ACTION_FAVORITE_MAPS";
    private static final String EXTRA_DATA = "com.crittermap.backcountrynavigator.xe.service.map.DATA";

    public BCMapIntentService() {
        super(BCMapIntentService.class.getSimpleName());
    }

    public static void startGetAllMapsByUser(Context context, String userName) {
        if (TextUtils.isEmpty(userName)) return;
        Intent intent = new Intent(context, BCMapIntentService.class);
        intent.setAction(ACTION_GET_MAPS);
        intent.putExtra(EXTRA_DATA, userName);
        context.startService(intent);
    }

    public static void startGetAllMapsLocal(Context context) {
        Intent intent = new Intent(context, BCMapIntentService.class);
        intent.setAction(ACTION_GET_MAPS_LOCAL);
        context.startService(intent);
    }

    public static void startGetDownloadedMapsLocal(Context context) {
        Intent intent = new Intent(context, BCMapIntentService.class);
        intent.setAction(ACTION_GET_DOWNLOADED_MAPS_LOCAL);
        context.startService(intent);
    }

    public static void startGetFavouriteMapsLocal(Context context) {
        Intent intent = new Intent(context, BCMapIntentService.class);
        intent.setAction(ACTION_FAVORITE_MAPS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            assert action != null;
            switch (action) {
                case ACTION_GET_MAPS:
                    handleActionGetMaps(intent.getStringExtra(EXTRA_DATA));
                    break;
                case ACTION_GET_MAPS_LOCAL:
                    handleActionGetMapsLocal();
                    break;
                case ACTION_GET_DOWNLOADED_MAPS_LOCAL:
                    handleActionGetDownloadedMapsLocal();
                    break;
                case ACTION_FAVORITE_MAPS:
                    handleActionFavoriteMaps();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleActionFavoriteMaps() {
        List<BCMap> localMaps = MapUtils.getFavouriteMaps();
        EventBus.getDefault().post(new BCLoadFavouriteMapsSuccessEvent(localMaps));
    }

    private void handleActionGetMaps(String email) {
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCMapResponse mapResponses = apiService.doLoadMapsByUser(email);
            List<BCMapSource> mapSources = mapResponses.getMapSources();
            List<BCMap> maps = MapUtils.mapAll(mapSources);
            ArrayList<BCMap> mapsNoLayer = new ArrayList<>();
            for (int i = 0; i < maps.size(); i++) {
                BCMap map = maps.get(i);
                if (!map.getVisibility().equals("layer")) {
                    mapsNoLayer.add(map);
                }
            }
            EventBus.getDefault().post(new BCLoadMapsSuccessEvent(mapsNoLayer));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BCLoadMapsFailedEvent());
        }
    }

    private void handleActionGetMapsLocal() {
        List<BCMap> localMaps = MapUtils.getAllMaps();
        EventBus.getDefault().post(new BCLoadMapsSuccessEvent(localMaps));
    }

    private void handleActionGetDownloadedMapsLocal() {
        List<BCMap> localMaps = MapUtils.getAllDownloadedMaps();
        EventBus.getDefault().post(new BCLoadDownloadMapsSuccessEvent(localMaps));
    }

    public static class BCChangeMapTabEvent {
        public String searchQuery;
        public int tabPosition;

        public BCChangeMapTabEvent(CharSequence query, int position) {
            searchQuery = query.toString();
            tabPosition = position;
        }
    }

    public static class BCSearchMapEvent {
        public String searchQuery;

        public BCSearchMapEvent(String query) {
            this.searchQuery = query;
        }
    }

    public static class BCAutoCompletedSearchMapEvent {
        public String mapName;

        public BCAutoCompletedSearchMapEvent(String mapName) {
            this.mapName = mapName;
        }
    }

    public class BCLoadMapsSuccessEvent {
        public List<BCMap> data;

        public BCLoadMapsSuccessEvent(List<BCMap> maps) {
            this.data = maps;
        }
    }

    public class BCLoadDownloadMapsSuccessEvent {
        public List<BCMap> data;

        public BCLoadDownloadMapsSuccessEvent(List<BCMap> maps) {
            this.data = maps;
        }
    }

    public class BCLoadMapsFailedEvent {

    }

    public class BCLoadFavouriteMapsSuccessEvent {
        public List<BCMap> data;

        public BCLoadFavouriteMapsSuccessEvent(List<BCMap> maps) {
            this.data = maps;
        }
    }
}

package com.crittermap.backcountrynavigator.xe.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_DatabaseHelper;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_EventNotification;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_CoverageGridLayer;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_ConverterUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_GlobalMapTiles;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_TilesDownloadNotification;
import com.crittermap.backcountrynavigator.xe.controller.utils.TTemplateServer;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileIDForService;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMapDBHelper;
import com.crittermap.backcountrynavigator.xe.eventbus.BCDownloadingMapProcess;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BC_TilesDownloadService extends IntentService {
    private static final String TILES_DOWNLOAD_ACTION = "com.saveondev.arcgiscontroller.controller.action.TILES_DOWNLOAD_ACTION";

    private static final String EXTRA_TILES_DOWNLOAD = "com.saveondev.arcgiscontroller.controller.extra.EXTRA_TILES_DOWNLOAD";
    private static final String LAYERS = "LAYERS";
    private static final String MAP_NAME = "MAP_NAME";
    private static final String MIN_ZOOM = "MIN_ZOOM";
    private static final String MAX_ZOOM = "MAX_ZOOM";
    public static final int MAX_FILE_PER_THREAD = 100;
    public static final int CORE_POOL_SIZE = 4;
    public static final int MAXIMUM_POOL_SIZE = 1000;
    public static final int KEEP_ALIVE_TIME = 10;
    private final String TAG = BC_TilesDownloadService.class.getSimpleName();

    private AtomicBoolean isStop = new AtomicBoolean(false);
    private LinkedList<Pair<TileID, String>> files = new LinkedList<>();
    private Collection<Future> features = new LinkedList<>();
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    private AtomicInteger successFiles;
    private AtomicInteger failedFiles;
    private String mapName = "";

    public BC_TilesDownloadService() {
        super("BC_TilesDownloadService");
        successFiles = new AtomicInteger(0);
        failedFiles = new AtomicInteger(0);
    }

    public static void startActionTilesDownload(Context context, ArrayList<TileID> list, ArrayList<BC_CoverageGridLayer> coverageGridLayers, String mapName, int minZoom, int maxZoom) {
        Intent intent = new Intent(context, BC_TilesDownloadService.class);
        intent.setAction(TILES_DOWNLOAD_ACTION);
        ArrayList<TileIDForService> convertList = new ArrayList<>();
        for (TileID tileID : list) {
            convertList.add(new TileIDForService(tileID));
        }
        Bundle extras = new Bundle();
        ArrayList<String> mapList = new ArrayList<>();
        for (BC_CoverageGridLayer l : coverageGridLayers) {
            mapList.add(l.getDbName());
        }
        extras.putSerializable(EXTRA_TILES_DOWNLOAD, convertList);
        extras.putStringArrayList(LAYERS, mapList);
        extras.putString(MAP_NAME, mapName);
        extras.putInt(MIN_ZOOM, minZoom);
        extras.putInt(MAX_ZOOM, maxZoom);
        intent.putExtras(extras);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            EventBus.getDefault().register(this);
            final String action = intent.getAction();
            if (TILES_DOWNLOAD_ACTION.equals(action)) {
                @SuppressWarnings("unchecked") final ArrayList<TileIDForService> list = (ArrayList<TileIDForService>) intent.getSerializableExtra(EXTRA_TILES_DOWNLOAD);
                @SuppressWarnings("unchecked") final List<String> layers = intent.getStringArrayListExtra(LAYERS);
                String mapName = intent.getStringExtra(MAP_NAME);
                int minZoom = intent.getIntExtra(MIN_ZOOM, 7);
                int maxZoom = intent.getIntExtra(MAX_ZOOM, 12);
                this.mapName = mapName;
                handleActionTilesDownload(getApplicationContext(), list, layers, mapName, minZoom, maxZoom);
            }
        }
    }

    private void handleActionTilesDownload(final Context mContext, ArrayList<TileIDForService> selectedTiles, List<String> maps, String mapName, int minZoom, int maxZoom) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        //lets go ahead and start foreground - as soon as we can figure out how to do it with out posting a duplicate notification.

//        startForeground(BC_TilesDownloadNotification.BC_TILES_DOWNLOAD_NOTIFICATION_ID,BC_TilesDownloadNotification.create(mContext, 0, 0, 100, 0));

        ArrayList<BC_CoverageGridLayer> coverageGridLayers = new ArrayList<>();
        for (String map : maps) {
            BCMap bcMap = BCMapDBHelper.getById(map);
            coverageGridLayers.add(new BC_CoverageGridLayer(bcMap));
        }
        int total = 0;
        ArrayList<ArrayList<ArrayList<TileID>>> arrayLists = new ArrayList<>();

        for (BC_CoverageGridLayer coverageGridLayer : coverageGridLayers) {
            ArrayList<ArrayList<TileID>> arrayGroup = new ArrayList<>();
            for (int i = minZoom; i <= maxZoom; i++) {
                arrayGroup.add(new ArrayList<TileID>());
            }
            String dbName = coverageGridLayer.getDbName();
            boolean isGMT = coverageGridLayer.isGMT();

            String dbPath = BC_Helper.getMBTilesPath(dbName);
            BC_DatabaseHelper databaseHelper = BC_DatabaseHelper.createInstance(dbPath, dbName, null);

            for (int i = 0; i < selectedTiles.size(); i++) {
                TileIDForService tile = selectedTiles.get(i);
                ArrayList<TileID> tilesToDownload = BC_GlobalMapTiles.getGoogleTilesToDownload(new TileID(tile), minZoom, maxZoom);

                //convert to TMS
                if (!isGMT) {
                    tilesToDownload = BC_ConverterUtils.convertTileIdList(tilesToDownload);
                }

                List<TileID> Exist = databaseHelper.getExistTiles(tilesToDownload);
                if (!(tilesToDownload.size() == Exist.size())) {
                    ArrayList<String> ExistString = new ArrayList<>();
                    for (TileID e : Exist) {
                        ExistString.add(e.getTileIdString());
                    }
                    for (TileID t : tilesToDownload) {
                        if (!ExistString.contains(t.getTileIdString())) {
                            arrayGroup.get(t.getLevel() - minZoom).add(t);
                            total++;
                        }
                    }
                }
            }
            arrayLists.add(arrayGroup);
        }

        if (total == 0) {
            BC_TilesDownloadNotification.notify(mContext, 0, total, total, 0);
            return;
        }
        successFiles.set(0);
        failedFiles.set(0);
        for (BC_CoverageGridLayer coverageGridLayer : coverageGridLayers) {
            String dbName = coverageGridLayer.getDbName();
            boolean isGMT = coverageGridLayer.isGMT();

            String dbPath = BC_Helper.getMBTilesPath(dbName);
            BC_DatabaseHelper databaseHelper = null;
            try {
                databaseHelper = BC_DatabaseHelper.createInstance(dbPath, dbName, null);
                databaseHelper.open();
                Log.v(TAG, "Start download: " + Calendar.getInstance().getTime().toString());

                ArrayList<ArrayList<TileID>> arrayGroup = arrayLists.get(coverageGridLayers.indexOf(coverageGridLayer));

                for (ArrayList<TileID> arrayList : arrayGroup) {
                    Log.v(TAG, "Start download with : " + arrayList.size() + " file");

                    if (isStop.get()) break;
                    for (TileID googleTile : arrayList) {
                        if (isStop.get()) break;
                        TileID tileToDownload = googleTile.copy();

                        if (!isGMT) {
                            tileToDownload = BC_GlobalMapTiles.tmsToGoole(googleTile);
                        }


                        if (!databaseHelper.isTileExistAsync(tileToDownload)) {
                            String url = (new TTemplateServer(coverageGridLayer.getBaseUrl())).getURLforTileID(googleTile);
                            files.add(new Pair(tileToDownload, url));
                            if (files.size() == MAX_FILE_PER_THREAD) {
                                LinkedList workingList = Lists.newLinkedList(files);
                                BC_TilesDownloadWorker worker = new BC_TilesDownloadWorker(workingList, databaseHelper, isStop, successFiles, failedFiles, mContext, total, mapName);
                                features.add(executor.submit(worker));
                                files = new LinkedList<>();
                            }
                        } else {
                            Log.v(TAG, "Skip!!!Tile exist");
                        }
                    }
                    //last pool
                    LinkedList workingList = Lists.newLinkedList(files);
                    BC_TilesDownloadWorker worker = new BC_TilesDownloadWorker(workingList, databaseHelper, isStop, successFiles, failedFiles, mContext, total, mapName);
                    features.add(executor.submit(worker));
                    files = new LinkedList<>();
                }

                for (Future future : features) {
                    try {
                        if (isStop.get()) {
                            break;
                        } else {
                            future.get();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }
                }
                Log.v(TAG, "End download: " + Calendar.getInstance().getTime().toString());

            } catch (Exception exception) {
                Log.e(TAG, exception.getMessage());
                Log.e(TAG, "Ex download: " + Calendar.getInstance().getTime().toString());
            } finally {
                if (databaseHelper != null) {
                    databaseHelper.close();
                }

            }
        }
        if (wakeLock.isHeld())
            wakeLock.release();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (isStop.get()) {
            BC_TilesDownloadNotification.cancel(this);
        } else {
            int total = successFiles.get() + failedFiles.get();
            BC_TilesDownloadNotification.notify(this, 0, total, total, failedFiles.get());
            EventBus.getDefault().postSticky(new BCDownloadingMapProcess(successFiles.get(), total, failedFiles.get(), this.mapName));
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(BC_EventNotification event) {
        this.isStop.set(true);
        executor.shutdownNow();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Future future : features) {
            if (!future.isCancelled()) {
                future.cancel(true);
            }
        }
        BC_TilesDownloadNotification.cancel(this);

        EventBus.getDefault().postSticky(new BCDownloadingMapProcess(successFiles.get(), successFiles.get() + failedFiles.get(), failedFiles.get(), this.mapName));

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}

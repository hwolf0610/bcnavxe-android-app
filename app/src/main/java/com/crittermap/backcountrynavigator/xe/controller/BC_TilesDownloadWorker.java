package com.crittermap.backcountrynavigator.xe.controller;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_DatabaseHelper;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_TilesDownloadNotification;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.eventbus.BCDownloadingMapProcess;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BC_TilesDownloadWorker implements Runnable {
    private final String TAG = BC_TilesDownloadWorker.class.getSimpleName();
    private Collection<Pair<TileID, String>> files;
    private BC_DatabaseHelper databaseHelper;
    private AtomicInteger successFiles;
    private AtomicInteger failedFiles;
    private Context mContext;
    private int total;
    private String mapName;
    private AtomicBoolean isStop;

    public BC_TilesDownloadWorker(Collection<Pair<TileID, String>> files,
                                  BC_DatabaseHelper databaseHelper, AtomicBoolean isStop,
                                  AtomicInteger successFiles, AtomicInteger failedFiles,
                                  Context mContext,
                                  int total,
                                  String mapName) {
        this.files = files;
        this.databaseHelper = databaseHelper;
        this.successFiles = successFiles;
        this.failedFiles = failedFiles;
        this.mContext = mContext;
        this.total = total;
        this.mapName = mapName;
        this.isStop = isStop;
    }

    @Override
    public void run() {
        Log.v(TAG, String.format("Start download on thread: %s, with %d files at %s", Thread.currentThread().getName(), files.size(), Calendar.getInstance().getTime().toString()));
        databaseHelper.open();
        for (Pair<TileID, String> fileFutureTarget : files) {
            if (!isStop.get()) {
                try {
                    URL url = new URL(fileFutureTarget.second);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "BCN");
                    urlConnection.setRequestProperty("Referer","https://www.bcnavxe.com");
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != 200) {
                        Log.e(TAG, "Error when download file: " + url);
                        failedFiles.getAndIncrement();
                    } else {
                        byte[] data;
                        try {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            byte[] chunk = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = in.read(chunk)) > 0) {
                                outputStream.write(chunk, 0, bytesRead);
                            }

                            outputStream.close();
                            in.close();

                            data = outputStream.toByteArray();
                        } finally {
                            urlConnection.disconnect();
                        }

                        TileID tileID = fileFutureTarget.first.copy();
                        tileID.setData(data);
                        Log.v(TAG, String.format("Download complete on thread: %s, with TileID : %s", Thread.currentThread().getName(), tileID.toString()));
                        databaseHelper.insertNewTile(tileID);
                        successFiles.getAndIncrement();
                    }

                    if (successFiles.get() % 5 == 0 || (failedFiles.get() > 0 && failedFiles.get() % 2 == 0) || successFiles.get() >= total) {
                        BC_TilesDownloadNotification.notify(mContext, 0, successFiles.get(), total, failedFiles.get());
                        EventBus.getDefault().postSticky(new BCDownloadingMapProcess(successFiles.get(), total, failedFiles.get(), mapName));
                    }

                    Log.v(TAG, String.format("Download %s on thread %s,  number success : %d, number failed : %d, in total : %d", fileFutureTarget.first.toString(), Thread.currentThread().getName(), successFiles.get(), failedFiles.get(), total));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());

                    failedFiles.getAndIncrement();
                }
            }
            Log.v(TAG, String.format("Finish download on thead: %s, with %d files at %s", Thread.currentThread().getName(), files.size(), Calendar.getInstance().getTime().toString()));
        }
    }
}

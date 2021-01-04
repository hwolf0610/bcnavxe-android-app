package com.crittermap.backcountrynavigator.xe.ui.splashScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_DatabaseHelper;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.core.api.dto.Application;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.core.domain.application.FetchApplicationUseCase;
import com.crittermap.backcountrynavigator.xe.core.domain.application.UpdateApplicationUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseDaggerActivity;
import com.crittermap.backcountrynavigator.xe.ui.BCMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by henryhai on 3/8/18.
 */

public class BCSplashScreenActivity extends BCBaseDaggerActivity {

    private static final String TAG = BCSplashScreenActivity.class.getSimpleName();
    private int DELAY_TIME = 2000; // 2 seconds

    @Inject
    @Named("appVersionInt")
    int appVersionInt;

    @Inject
    @Named("appVersion")
    String appVersionString;

    @Inject
    FetchApplicationUseCase fetchApplicationUseCase;

    @Inject
    UpdateApplicationUseCase updateApplicationUseCase;

    @Inject
    ThreadExecutor threadExecutor;

    @Inject
    PostExecutionThread postExecutionThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        fetchApplicationUseCase.execute(null, new Consumer<Application>() {
            @Override
            public void accept(final Application application) {
                int previousAppVersionInt = Integer.parseInt(application.getAppVersion().replaceAll("\\.", ""));
                if (previousAppVersionInt < appVersionInt) {
                    doUpgradeVersion(
                            previousAppVersionInt,
                            new Action() {
                                @Override
                                public void run() {
                                    application.setAppVersion(appVersionString);
                                    updateApplicationUseCase.execute(application, new Action() {
                                        @Override
                                        public void run() {
                                            safeCheckDeepLink();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) {
                                            //FIXME handle to login page?
                                            handleDefaultError(throwable);
                                        }
                                    });
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    //FIXME handle to login page?
                                    handleDefaultError(throwable);
                                }
                            });
                } else {
                    safeCheckDeepLink();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                //FIXME handle to login page?
                handleDefaultError(throwable);
            }
        });
    }

    private void handleDefaultError(Throwable throwable) {
        throwable.printStackTrace();
        Log.e(TAG, throwable.getMessage());
        goToAnotherScreen();
    }

    private void doUpgradeVersion(int previousAppVersionInt, Action success, Consumer<Throwable> error) {
        if (previousAppVersionInt < 126) {
            doUpgradeVersion126(success, error);
        } else {
            safeCheckDeepLink();
        }
    }

    private void doUpgradeVersion126(Action success, Consumer<Throwable> error) {
        List<Completable> completableList = new ArrayList<>();
        List<BCMap> downloadedMaps = MapUtils.getAllDownloadedMaps();
        for (final BCMap map : downloadedMaps) {
            completableList.add(Completable.create(new CompletableOnSubscribe() {
                @Override
                public void subscribe(CompletableEmitter emitter) {
                    try {
                        String dbName = map.getId();
                        String dbPath = BC_Helper.getMBTilesPath(dbName);
                        BC_DatabaseHelper databaseHelper = BC_DatabaseHelper.createInstance(dbPath, dbName, null);
                        databaseHelper.updateTileIDsForVersion126();
                        emitter.onComplete();
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
            })
                    .subscribeOn(threadExecutor.getScheduler())
                    .observeOn(postExecutionThread.getScheduler()));
        }
        Completable.merge(completableList).subscribeOn(threadExecutor.getScheduler())
                .observeOn(postExecutionThread.getScheduler()).subscribe(success, error);
    }

    private void safeCheckDeepLink() {
            Intent appLinkIntent = getIntent();
            Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null) {
            String fullUrl = appLinkData.toString();
            fullUrl = fullUrl.replace("https://", "");
            fullUrl = fullUrl.replace("http://", "");
            if (fullUrl.contains("bcnavxe.com/#/trip?trp=")) {
                String tripId = fullUrl.replace("bcnavxe.com/#/trip?trp=", "").split("&")[0];
                BCUtils.goToHome(this, tripId);
            } else if (fullUrl.contains("bcnavxe.com/#/trip/")) {
                String tripId = fullUrl.replace("bcnavxe.com/#/trip/", "").split("&")[0];
                BCUtils.goToHome(this, tripId);
            } else {
                goToAnotherScreen();
            }
        } else {
            goToAnotherScreen();
        }
    }

    /*
        New delay time to go to another screen if have
     */
    private void goToAnotherScreen() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(BCSplashScreenActivity.this, BCMainActivity.class);
                startActivity(intent);
                finishScreen();
            }
        };
        Timer t = new Timer();
        t.schedule(task, DELAY_TIME);
    }

    /*
        Destroy this screen in case of user press back button
     */
    private void finishScreen() {
        this.finish();
    }
}

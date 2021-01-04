package com.crittermap.backcountrynavigator.xe.core.domain;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class JobExecutor extends ThreadExecutor {
    private ThreadPoolExecutor threadPoolExecutor = createExecutor();

    @Override
    public Scheduler getScheduler() {
        return Schedulers.from(this);
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        this.threadPoolExecutor.execute(runnable);
    }

    private ThreadPoolExecutor createExecutor() {
        return new ThreadPoolExecutor(3, 5, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue(), new JobThreadFactory());
    }

    private class JobThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, "android_" + counter++);
        }
    }
}

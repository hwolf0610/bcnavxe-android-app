package com.crittermap.backcountrynavigator.xe.core.domain;

import java.util.concurrent.Executor;

import io.reactivex.Scheduler;

/**
 * Executor implementation can be based on different frameworks or techniques of asynchronous
 * execution, but every implementation will execute the
 * [AbstractSingleUseCase] out of the UI thread.
 */
public abstract class ThreadExecutor implements Executor {
    private Scheduler scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }
}

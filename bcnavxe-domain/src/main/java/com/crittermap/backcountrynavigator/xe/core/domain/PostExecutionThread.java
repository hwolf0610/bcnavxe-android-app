package com.crittermap.backcountrynavigator.xe.core.domain;

import io.reactivex.Scheduler;

public abstract class PostExecutionThread {
    private Scheduler scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }
}

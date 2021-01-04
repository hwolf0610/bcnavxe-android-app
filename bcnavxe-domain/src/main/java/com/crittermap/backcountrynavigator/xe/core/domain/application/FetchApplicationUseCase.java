package com.crittermap.backcountrynavigator.xe.core.domain.application;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.Application;
import com.crittermap.backcountrynavigator.xe.core.data.ApplicationSharedPrefRepository;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.core.domain.base.AbstractSingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;

public class FetchApplicationUseCase extends AbstractSingleUseCase<Application> {
    private String TAG = FetchApplicationUseCase.class.getSimpleName();
    private ApplicationSharedPrefRepository applicationRepository;

    @Inject
    public FetchApplicationUseCase(ApplicationSharedPrefRepository applicationRepository,
                                   ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.applicationRepository = applicationRepository;
    }

    @Override
    protected Single build(@Nullable Application params) {
        return Single.just(applicationRepository.get())
                .doOnSuccess(new Consumer<Application>() {
                    @Override
                    public void accept(Application application) {
                        Log.d(TAG, "Fetch application success");
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "Fetch application failed");
                    }
                });
    }
}

package com.crittermap.backcountrynavigator.xe.core.domain.application;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.Application;
import com.crittermap.backcountrynavigator.xe.core.data.ApplicationSharedPrefRepository;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.core.domain.base.AbstractCompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class UpdateApplicationUseCase extends AbstractCompletableUseCase<Application> {
    private String TAG = UpdateApplicationUseCase.class.getSimpleName();
    private ApplicationSharedPrefRepository applicationSharedPrefRepository;

    @Inject
    public UpdateApplicationUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, ApplicationSharedPrefRepository applicationSharedPrefRepository) {
        super(threadExecutor, postExecutionThread);
        this.applicationSharedPrefRepository = applicationSharedPrefRepository;
    }

    @Override
    protected Completable build(Application params) {
        return Single.just(applicationSharedPrefRepository.persist(params))
                .doOnSuccess(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean application) {
                        Log.d(TAG, "Application persist");
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage());
                    }
                }).flatMapCompletable(new Function<Boolean, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Boolean result) {
                        return new CompletableSource() {
                            @Override
                            public void subscribe(CompletableObserver cs) {
                                cs.onComplete();
                            }
                        };
                    }
                });
    }
}

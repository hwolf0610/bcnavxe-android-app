package com.crittermap.backcountrynavigator.xe.core.domain.base;

import android.support.annotation.Nullable;

import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;

public abstract class AbstractSingleUseCase<T> extends BaseAbstractUseCase {
    protected ThreadExecutor threadExecutor;
    protected PostExecutionThread postExecutionThread;

    public AbstractSingleUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Builds an [Single] which will be used when executing the current [AbstractSingleUseCase].
     */
    protected abstract Single build(@Nullable T params);

    /**
     * Executes the current use case.
     *
     * @param observer [DisposableSingleObserver] which will be listening to the observable build
     *                 * by [.buildInteractorSingle] ()} method.
     *                 *
     * @param params   Parameters (Optional) used to build/execute this use case.
     */
    public void execute(final DisposableSingleObserver observer, T params) {
        execute(params,
                new Consumer() {
                    @Override
                    public void accept(Object v) {
                        observer.onSuccess(v);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        observer.onError(throwable);
                    }
                });
    }

    /**
     * Executes the current use case.
     *
     * @param params     Parameters (Optional) used to build/execute this use case.
     * @param onComplete which will be listening to the completable
     * @param onError    function which will be listening to the single errors
     */
    @SuppressWarnings("unchecked")
    public void execute(T params, final Consumer onComplete, final Consumer<Throwable> onError) {
        Single single = this.build(params)
                .subscribeOn(threadExecutor.getScheduler())
                .observeOn(postExecutionThread.getScheduler());
        addDisposable(single.subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                onComplete.accept(o);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (!allDisposed()) onError.accept(throwable);
            }
        }));
    }
}

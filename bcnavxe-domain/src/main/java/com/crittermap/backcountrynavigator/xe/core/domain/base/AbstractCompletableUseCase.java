package com.crittermap.backcountrynavigator.xe.core.domain.base;

import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;

public abstract class AbstractCompletableUseCase<T> extends BaseAbstractUseCase {
    protected ThreadExecutor threadExecutor;
    protected PostExecutionThread postExecutionThread;

    public AbstractCompletableUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Builds an [Completable] which will be used when executing the current [AbstractCompletableUseCase].
     */
    protected abstract Completable build(T params);

    /**
     * Executes the current use case.
     *
     * @param observer [DisposableCompletableObserver] which will be listening to the observable build
     *                 * by [.buildUseCaseObservable] ()} method.
     *                 *
     * @param params   Parameters (Optional) used to build/execute this use case.
     */
    public void execute(final DisposableCompletableObserver observer, T params) {
        execute(params,
                new Action() {
                    @Override
                    public void run() {
                        observer.onComplete();
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
     * @param onError    function which will be listening to the completable errors
     */
    public void execute(T params, Action onComplete, final Consumer<Throwable> onError) {
        Completable completable = this.build(params)
                .subscribeOn(threadExecutor.getScheduler())
                .observeOn(postExecutionThread.getScheduler());
        addDisposable(completable.subscribe(onComplete, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (!allDisposed()) onError.accept(throwable);
            }
        }));
    }
}

package com.crittermap.backcountrynavigator.xe.core.domain.base;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseAbstractUseCase {
    private CompositeDisposable disposables = new CompositeDisposable();
    /**
     * Dispose from current [CompositeDisposable].
     */
    void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    void clear() {
        if (!disposables.isDisposed()) {
            disposables.clear();
        }
    }

    /**
     * Used for test validation
     */
    protected Boolean allDisposed() {
        return disposables.isDisposed();
    }

    protected void addDisposable(io.reactivex.disposables.Disposable disposable) {
        Preconditions.checkNotNull(disposable);
        Preconditions.checkNotNull(disposables);
        disposables.add(disposable);
    }

    class MissingParamsException extends IllegalArgumentException {
        public MissingParamsException() {
            super("Params are missing from call");
        }
    }
}

package com.crittermap.backcountrynavigator.xe.core.domain.base;

public class Preconditions {
    static void checkNotNull(Object reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
    }

    void checkNotDisposed(BaseAbstractUseCase useCase) {
        if (useCase.allDisposed()) {
            throw new IllegalStateException("Use case $useCase has been disposed and should not be re-used.");
        }
    }
}

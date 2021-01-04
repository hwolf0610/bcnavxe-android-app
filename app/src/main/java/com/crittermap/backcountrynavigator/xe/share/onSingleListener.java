package com.crittermap.backcountrynavigator.xe.share;

public interface onSingleListener<T> {
    void onSuccess(T result);

    void onFailed(String message);
}

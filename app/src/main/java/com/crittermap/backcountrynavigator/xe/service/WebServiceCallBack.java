package com.crittermap.backcountrynavigator.xe.service;

public interface WebServiceCallBack<T> {
    void onSuccess(T data);

    void onFailed(String errorMessage);
}

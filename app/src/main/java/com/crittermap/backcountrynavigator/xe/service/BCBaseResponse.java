package com.crittermap.backcountrynavigator.xe.service;

/**
 * Created by thutrang.dao on 3/21/18.
 */

public class BCBaseResponse<T> {
    private int responseCode;
    private String responseText;
    private T payload;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}

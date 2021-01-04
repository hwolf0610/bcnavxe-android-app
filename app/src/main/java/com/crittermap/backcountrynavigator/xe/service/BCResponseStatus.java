package com.crittermap.backcountrynavigator.xe.service;

/**
 * Created by nhatdear on 3/10/18.
 */

public class BCResponseStatus {
    private String status;
    private String data;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.crittermap.backcountrynavigator.xe.eventbus;

public class BCAddNewWayPointSuccessEvent {
    private Exception exception;

    public BCAddNewWayPointSuccessEvent(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}

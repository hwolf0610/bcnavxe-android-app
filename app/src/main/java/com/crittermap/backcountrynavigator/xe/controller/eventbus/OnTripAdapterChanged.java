package com.crittermap.backcountrynavigator.xe.controller.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;

import java.util.List;

public class OnTripAdapterChanged {
    private final List<BCTripInfo> arr;

    public OnTripAdapterChanged(List<BCTripInfo> mergeTrip) {
        this.arr = mergeTrip;
    }

    public List<BCTripInfo> getArr() {
        return arr;
    }
}

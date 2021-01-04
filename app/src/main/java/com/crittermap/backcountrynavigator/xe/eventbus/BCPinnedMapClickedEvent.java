package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;

public class BCPinnedMapClickedEvent {
    private BCMap map;

    public BCPinnedMapClickedEvent(BCMap map) {
        this.map = map;
    }

    public BCMap getMap() {
        return map;
    }
}

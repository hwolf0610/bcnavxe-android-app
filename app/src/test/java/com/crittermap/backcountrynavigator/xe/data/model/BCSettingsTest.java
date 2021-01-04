package com.crittermap.backcountrynavigator.xe.data.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BCSettingsTest {

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        BCSettings settings = new BCSettings();
        BCSettings cloneSettings = settings.clone();
        assertNotNull(cloneSettings);
        assertEquals(cloneSettings.getId(), settings.getId());
        assertEquals(cloneSettings.getArea(), settings.getArea());
    }
}
package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;

import java.util.ArrayList;

/**
 * Created by nhatdear on 4/15/18.
 */

public class BCSketchEditorChangeEvent {
    private boolean canRedo = false;
    private boolean canUndo = false;
    private ArrayList<BCGeometry> tempGeometries;

    public BCSketchEditorChangeEvent(boolean canUndo, boolean canRedo, ArrayList<BCGeometry> tempGeometries) {
        this.canRedo = canRedo;
        this.canUndo = canUndo;
        this.tempGeometries = tempGeometries;
    }

    public boolean isCanRedo() {
        return canRedo;
    }

    public void setCanRedo(boolean canRedo) {
        this.canRedo = canRedo;
    }

    public boolean isCanUndo() {
        return canUndo;
    }

    public void setCanUndo(boolean canUndo) {
        this.canUndo = canUndo;
    }

    public ArrayList<BCGeometry> getTempGeometries() {
        return tempGeometries;
    }

    public void setTempGeometries(ArrayList<BCGeometry> tempGeometries) {
        this.tempGeometries = tempGeometries;
    }
}

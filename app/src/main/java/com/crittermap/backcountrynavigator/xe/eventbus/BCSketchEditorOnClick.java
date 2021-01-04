package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCSketchControllerFragment;

/**
 * Created by nhatdear on 4/15/18.
 */

public class BCSketchEditorOnClick {
    BCSketchControllerFragment.SketchAction action;

    public BCSketchEditorOnClick(BCSketchControllerFragment.SketchAction action) {
        this.action = action;
    }

    public BCSketchControllerFragment.SketchAction getAction() {
        return action;
    }

    public void setAction(BCSketchControllerFragment.SketchAction action) {
        this.action = action;
    }
}

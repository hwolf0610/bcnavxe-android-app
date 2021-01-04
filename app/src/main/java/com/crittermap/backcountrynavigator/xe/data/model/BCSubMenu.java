package com.crittermap.backcountrynavigator.xe.data.model;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by henry on 3/20/2018.
 */

public class BCSubMenu extends BaseModel {
    private String name;
    private String status;

    public BCSubMenu(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public BCSubMenu() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

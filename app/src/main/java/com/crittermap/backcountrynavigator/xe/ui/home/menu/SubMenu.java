package com.crittermap.backcountrynavigator.xe.ui.home.menu;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by henryhai on 3/17/18.
 */

public class SubMenu implements Parcelable {

    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public SubMenu(String name) {
        this.mName = name;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
    }

    protected SubMenu(Parcel in) {
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<SubMenu> CREATOR = new Parcelable.Creator<SubMenu>() {
        @Override public SubMenu createFromParcel(Parcel source) {
            return new SubMenu(source);
        }

        @Override public SubMenu[] newArray(int size) {
            return new SubMenu[size];
        }
    };
}

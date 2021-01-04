package com.crittermap.backcountrynavigator.xe.ui.home.menu;

import com.crittermap.backcountrynavigator.xe.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

/**
 * Created by henryhai on 3/17/18.
 */

public class BCTitleMenu extends ExpandableGroup<SubMenu> {

    private int mImageResource;
    private MenuType mMenuType;
    private String mMenuTitle;

    public enum MENU_TITLE {
        PLAN("Plan"),
        BOOKMARK("Bookmarks"),
        MAP_SOURCE("Map source"),
        ADD_ON("Add-on"),
        SHOP("shop"),
        GO_ONLINE("Go online"),
        GO_OFFLINE("Go offline"),
        ACCOUNT("Account"),
        SETTING("Settings"),
        HELP("Help"),
        LOG_OUT("Logout");

        private String value;

        MENU_TITLE(String value) {
            this.value = value;
        }

        public String value(){
            return this.value;
        }

        public static MENU_TITLE findMenuTitle(String menuTitle) {
            for (MENU_TITLE menu : values()) {
                if (menu.value().equals(menuTitle)) {
                    return menu;
                }
            }
            return null;
        }
    }


    public enum MenuType {
        DROP_DOWN, TOGGLE, NONE
    }

    public BCTitleMenu(String title, List<SubMenu> items, int imageResource, MenuType menuType) {
        super(title, items);
        mImageResource = imageResource;
        mMenuType = menuType;
        mMenuTitle = title;
    }

    public BCTitleMenu(String title, int imageResource, MenuType menuType) {
        super(title, null);
        mImageResource = imageResource;
        mMenuType = menuType;
        mMenuTitle = title;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public MenuType getMenuType() {
        return mMenuType;
    }

    public String getMenuTitle() {
        return mMenuTitle;
    }
}
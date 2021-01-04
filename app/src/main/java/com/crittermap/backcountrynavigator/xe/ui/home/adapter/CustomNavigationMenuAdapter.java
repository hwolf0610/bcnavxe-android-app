package com.crittermap.backcountrynavigator.xe.ui.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.home.menu.BCTitleMenu;
import com.crittermap.backcountrynavigator.xe.ui.home.menu.SubMenu;
import com.crittermap.backcountrynavigator.xe.ui.home.viewHolder.BCSubTitleViewHolder;
import com.crittermap.backcountrynavigator.xe.ui.home.viewHolder.BCTitleViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by henryhai on 3/17/18.
 */

public class CustomNavigationMenuAdapter
        extends ExpandableRecyclerViewAdapter<BCTitleViewHolder, BCSubTitleViewHolder> {

    private Context mContext;
    private OnItemClicked mListener;

    public CustomNavigationMenuAdapter(Context context, List<BCTitleMenu> titleMenuList,
                                       Activity activity) {
        super(titleMenuList);
        mContext = context;
        mListener = (OnItemClicked) activity;
    }

    @Override
    public BCTitleViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent, false);
        return new BCTitleViewHolder(view);
    }

    @Override
    public BCSubTitleViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_child, parent, false);
        return new BCSubTitleViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(BCSubTitleViewHolder holder, int flatPosition,
                                      final ExpandableGroup group, final int childIndex) {
        final SubMenu subMenu = ((BCTitleMenu) group).getItems().get(childIndex);
        holder.setChildTextView(subMenu.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChildClicked(childIndex, ((BCTitleMenu) group).getMenuTitle());
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(final BCTitleViewHolder holder, final int flatPosition,
                                      ExpandableGroup group) {
        holder.setMenuTitle(mContext, group);
        if (group instanceof BCTitleMenu) {
            if (((BCTitleMenu) group).getMenuType().equals(BCTitleMenu.MenuType.NONE)
                    || ((BCTitleMenu) group).getMenuType().equals(BCTitleMenu.MenuType.TOGGLE)) {
                holder.setOnGroupClickListener(new OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(int flatPos) {
                        Switch switchButton = holder.mSwitch;
                        if (switchButton.isChecked()) {
                            switchButton.setChecked(false);
                        } else {
                            switchButton.setChecked(true);
                        }
                        mListener.onGroupClicked(flatPos);
                        return false;
                    }
                });
            }
        }
    }

    public interface OnItemClicked {
        void onChildClicked(int position, String menuTitle);

        void onGroupClicked(int position);
    }
}
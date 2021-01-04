package com.crittermap.backcountrynavigator.xe.ui.home.viewHolder;

import android.content.Context;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.home.menu.BCTitleMenu;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by henryhai on 3/17/18.
 */

public class BCTitleViewHolder extends GroupViewHolder {
    @BindView(R.id.list_item_icon) ImageView mTitleIcon;
    @BindView(R.id.list_item_name) TextView mTitleName;
    @BindView(R.id.list_item_arrow) ImageView mArrow;
    @BindView(R.id.list_item_switch) public Switch mSwitch;

    public BCTitleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void expand() {
        animateExpand();
    }

    @Override public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        mArrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        mArrow.setAnimation(rotate);
    }

    public void setMenuTitle(Context context, ExpandableGroup title) {
        if (title instanceof BCTitleMenu) {
            mTitleName.setText(title.getTitle());
            mTitleIcon.setImageDrawable(
                    context.getResources().getDrawable(((BCTitleMenu) title).getImageResource()));
            if (((BCTitleMenu) title).getMenuType() == BCTitleMenu.MenuType.NONE) {
            } else if (((BCTitleMenu) title).getMenuType() == BCTitleMenu.MenuType.TOGGLE) {
                mSwitch.setVisibility(View.VISIBLE);
            } else {
                mArrow.setVisibility(View.VISIBLE);
                mArrow.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.ic_arrow_down));
            }
        }
    }
}

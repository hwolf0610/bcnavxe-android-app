package com.crittermap.backcountrynavigator.xe.ui.home.viewHolder;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crittermap.backcountrynavigator.xe.R;

/**
 * Created by henryhai on 3/17/18.
 */

public class BCSubTitleViewHolder
        extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {

    @BindView(R.id.list_item_child_name) TextView mChildTextView;

    public BCSubTitleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setChildTextView(String name) {
        mChildTextView.setText(name);
    }
}

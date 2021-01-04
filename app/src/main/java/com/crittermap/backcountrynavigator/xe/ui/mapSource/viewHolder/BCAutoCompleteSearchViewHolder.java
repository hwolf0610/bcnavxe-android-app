package com.crittermap.backcountrynavigator.xe.ui.mapSource.viewHolder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.custom.BC_TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 5/14/2018.
 */

public class BCAutoCompleteSearchViewHolder extends RecyclerView.ViewHolder {
    public @BindView(R.id.auto_complete_search_tv)
    BC_TextView autoCompleteSearchTv;

    public @BindView(R.id.autoCompleteLayout)
    ConstraintLayout layout;

    public BCAutoCompleteSearchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

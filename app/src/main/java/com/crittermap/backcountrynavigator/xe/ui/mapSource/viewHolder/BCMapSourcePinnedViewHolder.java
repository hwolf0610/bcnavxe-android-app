package com.crittermap.backcountrynavigator.xe.ui.mapSource.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 4/6/2018.
 */

public class BCMapSourcePinnedViewHolder extends RecyclerView.ViewHolder {

    public @BindView(R.id.radioButton)
    RadioButton radioButton;
    public @BindView(R.id.textView)
    TextView mapSourcePinnedTextView;
    public @BindView(R.id.map_pinned_layout)
    LinearLayout mapPinnedLayout;

    public BCMapSourcePinnedViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}

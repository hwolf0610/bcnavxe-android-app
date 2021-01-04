package com.crittermap.backcountrynavigator.xe.ui.mapSource.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.custom.BC_TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 3/25/2018.
 */

public class BCMapSourceViewHolder extends RecyclerView.ViewHolder {
    public @BindView(R.id.tv_map_name)
    BC_TextView mapSourceName;
    public @BindView(R.id.map_source_action)
    ImageButton mapSourceAction;
    public @BindView(R.id.map_source_favourite)
    ImageButton mapSourceFavorite;
    public @BindView(R.id.map_membership)
    ImageView mapSourceMembership;
    public @BindView(R.id.imv_logo)
    ImageView mapSourceLogo;
    public @BindView(R.id.tv_tags)
    TextView mapSourceTag;

    @BindView(R.id.item_map_source)
    public LinearLayout itemMapSource;

    public BCMapSourceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

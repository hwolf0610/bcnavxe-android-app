package com.crittermap.backcountrynavigator.xe.ui.selecttrip;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;

public class SelectTripViewHolder extends RecyclerView.ViewHolder {
    protected TextView name;

    public SelectTripViewHolder(View itemView) {
        super(itemView);
        this.name = itemView.findViewById(R.id.tv_name);
    }
}

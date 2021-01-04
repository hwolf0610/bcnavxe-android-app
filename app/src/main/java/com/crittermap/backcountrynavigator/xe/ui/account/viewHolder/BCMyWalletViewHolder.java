package com.crittermap.backcountrynavigator.xe.ui.account.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 5/17/2018.
 */

public class BCMyWalletViewHolder extends RecyclerView.ViewHolder {
    public @BindView(R.id.date_txt)
    TextView dateTxt;
    public @BindView(R.id.cost_txt)
    TextView costTxt;
    public @BindView(R.id.map_txt)
    TextView mapTxt;
    public @BindView(R.id.map_download_btn)
    ImageButton mapDownloadBtn;

    public BCMyWalletViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
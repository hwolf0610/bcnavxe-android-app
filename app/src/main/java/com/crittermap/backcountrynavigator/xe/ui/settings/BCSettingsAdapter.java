package com.crittermap.backcountrynavigator.xe.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.settings.appearance.BCSettingsAppearanceActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.backup.BCSettingsBackupActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol.BCSettingsMapControlsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.mapdownload.BCSettingsMapDownloadActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.powersaving.BCSettingsPowerSavingActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.BCSettingsQuickAccessActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.unitformat.BCSettingsUnitFormatActivity;

import java.util.List;

class BCSettingsAdapter extends RecyclerView.Adapter<BCSettingsAdapter.BCSettingsViewHolder> {


    private List<BCSettingsViewModel> settings;
    private Context context;

    public BCSettingsAdapter(List<BCSettingsViewModel> settings, Context context) {
        this.settings = settings;
        this.context = context;
    }

    @NonNull
    @Override
    public BCSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
        return new BCSettingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BCSettingsViewHolder holder, int position) {
        final BCSettingsViewModel viewModel = settings.get(position);
        holder.tvTitle.setText(viewModel.getTitleId());

        if (TextUtils.isEmpty(viewModel.getSubtitle())) {
            holder.tvSubtitle.setVisibility(View.GONE);
        } else {
            holder.tvSubtitle.setVisibility(View.VISIBLE);
            holder.tvSubtitle.setText(viewModel.getSubtitle());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickEvent(viewModel);
            }
        });
    }

    private void onItemClickEvent(BCSettingsViewModel viewModel) {
        switch (viewModel.getTitleId()) {
            case R.string.settings_quick_access_title:
                openActivity(BCSettingsQuickAccessActivity.class);
                break;
            case R.string.settings_storage_title:
                openActivity(BCSettingsStorageActivity.class);
                break;
            case R.string.settings_power_saving_title:
                openActivity(BCSettingsPowerSavingActivity.class);
                break;
            case R.string.settings_map_download_title:
                openActivity(BCSettingsMapDownloadActivity.class);
                break;
            case R.string.settings_map_controls_title:
                openActivity(BCSettingsMapControlsActivity.class);
                break;
            case R.string.settings_appearance_title:
                openActivity(BCSettingsAppearanceActivity.class);
                break;
            case R.string.settings_unit_format_title:
                openActivity(BCSettingsUnitFormatActivity.class);
                break;
            case R.string.settings_backup_title:
                openActivity(BCSettingsBackupActivity.class);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return settings.size();
    }

    private void openActivity(Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    class BCSettingsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvSubtitle;

        BCSettingsViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_setting_title);
            tvSubtitle = itemView.findViewById(R.id.tv_setting_subtitle);
        }
    }
}

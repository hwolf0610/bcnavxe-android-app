package com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.viewHolder.BCMapSourcePinnedViewHolder;

import java.util.List;

/**
 * Created by henry on 4/6/2018.
 */

public class BCMapSourcePinnedAdapter extends RecyclerView.Adapter<BCMapSourcePinnedViewHolder> {

    private List<BCMap> mMapSources;
    private int checkedPosition = -1;
    private String lastMapId;
    private OnPinnedMapChangedListener listener;

    public BCMapSourcePinnedAdapter(List<BCMap> mapSources, OnPinnedMapChangedListener listener) {
        mMapSources = mapSources;
        BCMap bcMap = MapUtils.getDefaultOrLastMap();
        if (bcMap != null) lastMapId = bcMap.getId();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BCMapSourcePinnedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.map_source_pinned_item, parent, false);
        return new BCMapSourcePinnedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BCMapSourcePinnedViewHolder holder, final int position) {
        final BCMap map = mMapSources.get(position);
        holder.mapSourcePinnedTextView.setText(map.getMapName());
        holder.mapPinnedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition != position) {
                    checkedPosition = position;
                    lastMapId = map.getId();
                    BCMap map = mMapSources.get(position);
                    listener.onPinnedMapChanged(map);
                }
                notifyDataSetChanged();
            }
        });

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mapPinnedLayout.performClick();
                holder.radioButton.setChecked(true);
            }
        });

        if (map.getId().equals(lastMapId)) {
            holder.radioButton.setChecked(true);
            checkedPosition = position;
        } else {
            holder.radioButton.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mMapSources.size();
    }

    public interface OnPinnedMapChangedListener {
        void onPinnedMapChanged(BCMap bcMap);
    }
}

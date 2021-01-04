package com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.viewHolder.BCAutoCompleteSearchViewHolder;

import java.util.List;

/**
 * Created by henry on 5/14/2018.
 */

public class BCAutoCompleteSearchAdapter extends RecyclerView.Adapter<BCAutoCompleteSearchViewHolder> {
    private List<BCMap> mMapSources;
    private OnItemClicked mListener;

    public BCAutoCompleteSearchAdapter(List<BCMap> mapSources, OnItemClicked listener) {
        mMapSources = mapSources;
        mListener = listener;
    }

    @NonNull
    @Override
    public BCAutoCompleteSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_complete_search_item, parent, false);
        return new BCAutoCompleteSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BCAutoCompleteSearchViewHolder holder, int position) {
        final BCMap map = mMapSources.get(position);
        holder.autoCompleteSearchTv.setText(map.getMapName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemSelected(map.getMapName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMapSources.size();
    }

    public interface OnItemClicked {
        void onItemSelected(String mapName);
    }
}

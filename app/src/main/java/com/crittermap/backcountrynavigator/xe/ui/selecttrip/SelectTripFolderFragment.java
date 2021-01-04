package com.crittermap.backcountrynavigator.xe.ui.selecttrip;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.selecttrip.BCSelectTripActivity.OnItemClickedListener;

import java.util.ArrayList;
import java.util.List;

public class SelectTripFolderFragment extends Fragment {
    private OnItemClickedListener<String> listener;
    private List<String> folders = new ArrayList<>();
    private TripFolderAdapter tripFolderAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_trip_folder, container, false);

        tripFolderAdapter = new TripFolderAdapter(folders);

        RecyclerView folderRecycleView = view.findViewById(R.id.recycler_view_select_trip);
        folderRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        folderRecycleView.setAdapter(tripFolderAdapter);

        return view;
    }

    public void setFolders(List<String> folders) {
        this.folders.clear();
        this.folders = folders;
        if (tripFolderAdapter != null) {
            tripFolderAdapter.notifyDataSetChanged();
        }

    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
        this.listener = listener;
    }

    private class TripFolderAdapter extends RecyclerView.Adapter<SelectTripViewHolder> {

        List<String> folders;

        TripFolderAdapter(List<String> folders) {
            this.folders = folders;
        }

        @NonNull
        @Override
        public SelectTripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_trip_folder, parent, false);
            return new SelectTripViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectTripViewHolder holder, final int position) {
            holder.name.setText(folders.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(folders.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }
    }
}

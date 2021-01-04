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
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.ui.selecttrip.BCSelectTripActivity.OnItemClickedListener;

import java.util.ArrayList;
import java.util.List;

public class SelectTripFragment extends Fragment {
    private OnItemClickedListener<BCTripInfo> listener;
    private List<BCTripInfo> trips = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_trip_folder, container, false);

        TripInfoAdapter tripInfoAdapter = new TripInfoAdapter(trips);

        RecyclerView folderRecycleView = view.findViewById(R.id.recycler_view_select_trip);
        folderRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        folderRecycleView.setAdapter(tripInfoAdapter);

        return view;
    }

    public void setTrips(List<BCTripInfo> trips) {
        this.trips = trips;
    }


    public void setOnItemClickListener(OnItemClickedListener<BCTripInfo> listener) {
        this.listener = listener;
    }

    private class TripInfoAdapter extends RecyclerView.Adapter<SelectTripViewHolder> {

        List<BCTripInfo> trips;

        public TripInfoAdapter(List<BCTripInfo> folders) {
            this.trips = folders;
        }

        @NonNull
        @Override
        public SelectTripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_trip, parent, false);
            return new SelectTripViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectTripViewHolder holder, final int position) {
            holder.name.setText(trips.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(trips.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }
    }
}

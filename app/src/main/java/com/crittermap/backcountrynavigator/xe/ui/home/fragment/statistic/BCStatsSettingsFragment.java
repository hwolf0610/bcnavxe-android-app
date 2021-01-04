package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatSettingsFragmentInteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatSettingsFragmentPresenterImpl;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;

import java.util.ArrayList;
import java.util.List;

public class BCStatsSettingsFragment extends AppCompatDialogFragment implements BCStatSettingsFragmentContracts.View {

    public static String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatsSettingsFragment";
    private OnFragmentInteractionListener mListener;
    private BCStatSettingsFragmentContracts.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<BCStatisticModel> bcStatisticModelList = new ArrayList<>();

    public BCStatsSettingsFragment() {
        // Required empty public constructor
    }

    public static BCStatsSettingsFragment newInstance() {
        return new BCStatsSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bcstats_settings, container, false);
        mPresenter = new BCStatSettingsFragmentPresenterImpl(this, new BCStatSettingsFragmentInteractorImpl());
        initializeViews(view);
        initializeAdapters();
        initializeData();
        initializeEvents(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void displayUserStatisticSettings(List<BCStatisticModel> results) {
        bcStatisticModelList.clear();
        bcStatisticModelList.addAll(results);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void backToMaximized() {
        mListener.onOpenMaximizedStatClicked();
    }

    @Override
    public void backToMinimized() {
        mListener.onOpenMinimizedStatClicked();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initializeData() {
        mPresenter.loadUserStatisticSetting();
    }

    @Override
    public void initializeAdapters() {
        mAdapter = new BCStatSettingAdapter(bcStatisticModelList, mPresenter);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initializeViews(View view) {
        mRecyclerView = view.findViewById(R.id.rv_stats_settings);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void initializeEvents(View view) {
        view.findViewById(R.id.imb_back_to_maximized).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                mPresenter.onBtnBackToMaximizedClicked();
            }
        });

        view.findViewById(R.id.imb_back_to_minimized).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                mPresenter.onBtnBackToMinimizedClicked();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onOpenMinimizedStatClicked();

        void onOpenMaximizedStatClicked();
    }

    class BCStatSettingAdapter extends RecyclerView.Adapter<BCStatSettingAdapter.ViewHolder> {
        private List<BCStatisticModel> dataSet;
        private BCStatSettingsFragmentContracts.Presenter presenter;

        BCStatSettingAdapter(ArrayList<BCStatisticModel> dataSet, BCStatSettingsFragmentContracts.Presenter presenter) {
            this.dataSet = dataSet;
            this.presenter = presenter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_stat_settings_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final BCStatisticModel statisticModel = dataSet.get(position);
            holder.logo.setImageResource(statisticModel.getTypeIconId());
            holder.typeName.setText(statisticModel.getStat().getName());
            holder.isMinimized.setChecked(statisticModel.isShowOnMinimize());
            holder.isMaximized.setChecked(statisticModel.isShowOnFull());
            holder.isMinimized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    presenter.onSettingCheckboxClicked(statisticModel.getStat(), isChecked, holder.isMaximized.isChecked());
                }
            });

            holder.isMaximized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    presenter.onSettingCheckboxClicked(statisticModel.getStat(), holder.isMinimized.isChecked(), isChecked);
                }
            });

            if (position == 0) {
                ConstraintLayout parent = (ConstraintLayout) holder.logo.getParent();
                ConstraintSet set = new ConstraintSet();
                TextView textView = new TextView(parent.getContext());
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setId(View.generateViewId());
                textView.setText(R.string.on_minimize);
                parent.addView(textView);
                set.clone(parent);
                set.connect(textView.getId(), ConstraintSet.BOTTOM, holder.isMinimized.getId(), ConstraintSet.TOP, 8);
                set.connect(textView.getId(), ConstraintSet.LEFT, holder.isMinimized.getId(), ConstraintSet.LEFT, 0);
                set.connect(textView.getId(), ConstraintSet.RIGHT, holder.isMinimized.getId(), ConstraintSet.RIGHT, 0);

                set.applyTo(parent);
            }
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView logo;
            TextView typeName;
            CheckBox isMinimized;
            CheckBox isMaximized;

            public ViewHolder(View itemView) {
                super(itemView);
                this.logo = itemView.findViewById(R.id.imv_stats_logo);
                this.typeName = itemView.findViewById(R.id.tv_type_name);
                this.isMinimized = itemView.findViewById(R.id.cb_mini);
                this.isMaximized = itemView.findViewById(R.id.cb_max);
            }
        }
    }
}

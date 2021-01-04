package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingService;
import com.crittermap.backcountrynavigator.xe.share.DateTimeUtils;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentInteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl.STAT_MODE;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatDialogFragment;
import io.reactivex.functions.Consumer;

import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.share.BC_StatsUtils.buildDistanceStringBySettings;

public class BCStatFragment extends DaggerAppCompatDialogFragment implements BCStatFragmentContracts.View {

    private static final int DEFAULT_HIT_AREA_BUFFER = 200;
    private static final String STAT_MODE = "STAT_MODE";
    public static final String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatFragment";
    private GridView gridView;
    private TextView minimizedTvDistance;
    private TextView minimizedTvTotalTime;
    private TextView maximizedTvDistance;
    private TextView maximizedTvTotalTime;
    private TextView tvLocation;

    @Inject
    GetUserSettingsUseCase getUserSettingsUseCase;

    private ConstraintLayout expandLocationContainer;
    private ConstraintLayout minimizedBasicInforContainer;

    private BCMinimizedStatsAdapter minimizedStatsAdapter;
    private BCMaximizedStatsAdapter maximizedStatsAdapter;

    private List<BCStatisticModel> minimizedStatsList = new ArrayList<>();
    private List<BCStatisticModel> maximizedStatsList = new ArrayList<>();
    private OnStatsFragmentListener onStatsFragmentListener;
    private STAT_MODE statMode = BCStatFragmentPresenterImpl.STAT_MODE.MINI;

    private BCStatFragmentContracts.Presenter presenter;
    private BCSettings settings;

    public static BCStatFragment newInstance(STAT_MODE statMode) {
        BCStatFragment fragment = new BCStatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(STAT_MODE, statMode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bcstat, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            statMode = (STAT_MODE) bundle.getSerializable(STAT_MODE);
        }
        presenter = new BCStatFragmentPresenterImpl(this, new BCStatFragmentInteractorImpl());

        initializeViews(view);
        initializeEvents(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserSettingsUseCase.execute(new Consumer<SettingsDTO>() {
            @Override
            public void accept(SettingsDTO settingsDTO) {
                settings = (new BCSettings()).importFromDTO(settingsDTO);
                initializeAdapters();
                initializeData();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable o) {
                //FIXME Handle error
                Toast.makeText(getContext(), "Cannot get user settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initializeData() {
        gridView.setAdapter(minimizedStatsAdapter);
        if (statMode == BCStatFragmentPresenterImpl.STAT_MODE.MINI) {
            presenter.loadMinimizedUserStatisticSetting();
        } else {
            presenter.loadMaximizedUserStatisticSetting();
        }
    }

    @Override
    public void initializeAdapters() {
        minimizedStatsAdapter = new BCMinimizedStatsAdapter(getLayoutInflater(), minimizedStatsList, getContext());
        maximizedStatsAdapter = new BCMaximizedStatsAdapter(getLayoutInflater(), maximizedStatsList, getContext());
    }

    @Override
    public void initializeViews(View view) {
        gridView = view.findViewById(R.id.grid_stats_container);
        expandLocationContainer = view.findViewById(R.id.expandLocationContainer);
        minimizedBasicInforContainer = view.findViewById(R.id.minimizedBasicInforContainer);
        minimizedTvDistance = view.findViewById(R.id.tv_distance);
        minimizedTvTotalTime = view.findViewById(R.id.tv_total_time);
        maximizedTvDistance = view.findViewById(R.id.tv_distance_max);
        maximizedTvTotalTime = view.findViewById(R.id.tv_time_max);
        tvLocation = view.findViewById(R.id.tv_location);
    }

    @Override
    public void initializeEvents(View view) {
        final Button btnExpand = view.findViewById(R.id.btn_expand);
        increaseHitArea(btnExpand, DEFAULT_HIT_AREA_BUFFER);
        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBtnExpandClicked();
            }
        });

        view.findViewById(R.id.imb_stats_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBtnSettingsClicked();
            }
        });
    }

    private void increaseHitArea(final View viewToIncreaseHitArea, final int hitAreaBuffer) {
        final View parent = (View) viewToIncreaseHitArea.getParent();
        parent.post(new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                viewToIncreaseHitArea.getHitRect(rect);
                rect.top -= hitAreaBuffer;
                rect.left -= hitAreaBuffer;
                rect.bottom += hitAreaBuffer;
                rect.right += hitAreaBuffer;
                parent.setTouchDelegate(new TouchDelegate(rect, viewToIncreaseHitArea));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.onStatsFragmentListener = (OnStatsFragmentListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(BCTrackingService.LocationChangedEvent event) {
        if (statMode == BCStatFragmentPresenterImpl.STAT_MODE.MINI) {
            presenter.loadMinimizedUserStatisticSetting();
        } else {
            presenter.loadMaximizedUserStatisticSetting();
        }
    }

    @Override
    public void displayMinimizedUserStatisticSettings(final BCUserStatistic results) {
        statMode = BCStatFragmentPresenterImpl.STAT_MODE.MINI;
        expandLocationContainer.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                expandLocationContainer.setVisibility(View.GONE);
                tvLocation.setText(results.getLastLocation());
            }
        });
        minimizedBasicInforContainer.animate().alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                minimizedBasicInforContainer.setVisibility(View.VISIBLE);
            }
        });

        minimizedTvDistance.setText(buildDistanceStringBySettings(results.getLastDistance(), settings));

        Date now = Calendar.getInstance().getTime();
        Date last = Calendar.getInstance().getTime();
        last.setTime(results.getStartTime());

        minimizedTvTotalTime.setText(DateTimeUtils.printDifference(last, now));

        minimizedStatsList.clear();
        minimizedStatsList.addAll(results.getUserStatsList());
        if (!(gridView.getAdapter() instanceof BCMinimizedStatsAdapter)) {
            gridView.setNumColumns(3);
            gridView.setAdapter(minimizedStatsAdapter);
        }
        minimizedStatsAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayMaximizedUserStatisticSettings(BCUserStatistic results) {
        statMode = BCStatFragmentPresenterImpl.STAT_MODE.EXPAND;
        expandLocationContainer.animate().alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                expandLocationContainer.setVisibility(View.VISIBLE);
            }
        });

        minimizedBasicInforContainer.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                minimizedBasicInforContainer.setVisibility(View.GONE);
            }
        });

        maximizedTvDistance.setText(buildDistanceStringBySettings(results.getLastDistance(), settings));

        Date now = Calendar.getInstance().getTime();
        Date last = Calendar.getInstance().getTime();
        last.setTime(results.getStartTime());

        maximizedTvTotalTime.setText(DateTimeUtils.printDifference(last, now));

        maximizedStatsList.clear();
        maximizedStatsList.addAll(results.getUserStatsList());
        if (!(gridView.getAdapter() instanceof BCMaximizedStatsAdapter)) {
            gridView.setNumColumns(2);
            gridView.setAdapter(maximizedStatsAdapter);
        }
        maximizedStatsAdapter.notifyDataSetChanged();
    }

    @Override
    public void openSettings() {
        onStatsFragmentListener.openStatsSettings();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public STAT_MODE getStatMode() {
        return statMode;
    }

    public interface OnStatsFragmentListener {
        void openStatsSettings();
    }

    class BCMinimizedStatsAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<BCStatisticModel> bcStatisticModels;
        private Context context;

        BCMinimizedStatsAdapter(LayoutInflater inflater, List<BCStatisticModel> bcStatisticModels, Context context) {
            this.layoutInflater = inflater;
            this.bcStatisticModels = bcStatisticModels;
            this.context = context;
        }

        @Override
        public int getCount() {
            return bcStatisticModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.minimized_statistic_grid_item, null);
                holder = new ViewHolder();
                holder.logo = convertView.findViewById(R.id.imv_stats_logo);
                holder.content = convertView.findViewById(R.id.tv_stats_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            BCStatisticModel model = bcStatisticModels.get(position);
            holder.logo.setImageResource(model.getTypeIconId());
            holder.content.setText(String.valueOf(model.getContent()));

            return convertView;
        }

        class ViewHolder {
            ImageView logo;
            TextView content;
        }
    }

    class BCMaximizedStatsAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<BCStatisticModel> bcStatisticModels;
        private Context context;

        BCMaximizedStatsAdapter(LayoutInflater inflater, List<BCStatisticModel> bcStatisticModels, Context context) {
            this.layoutInflater = inflater;
            this.bcStatisticModels = bcStatisticModels;
            this.context = context;
        }

        @Override
        public int getCount() {
            return bcStatisticModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maximized_statistic_grid_item, null);
                holder = new ViewHolder();
                holder.logo = convertView.findViewById(R.id.imv_stats_logo);
                holder.title = convertView.findViewById(R.id.tv_stats_title);
                holder.content = convertView.findViewById(R.id.tv_stats_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            BCStatisticModel model = bcStatisticModels.get(position);
            holder.logo.setImageResource(model.getTypeIconId());
            holder.title.setText(model.getStat().getName());
            holder.content.setText(String.valueOf(model.getContent()));

            return convertView;
        }

        class ViewHolder {
            ImageView logo;
            TextView title;
            TextView content;
        }
    }
}

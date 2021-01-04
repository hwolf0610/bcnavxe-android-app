package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_OnTileTouchToDownloadEvent;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.TileID;
import com.crittermap.backcountrynavigator.xe.data.BCMembershipDataHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembershipType;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMapDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapLayer;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.MAP_STATUS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper.getZoomLevelByScale;


public class BCDownloadMapFragment extends Fragment {

    public static String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCDownloadMapFragment";
    final String LOG_TAG = "BCDownloadMapFragment";

    Unbinder mUnbinder;
    @BindView(R.id.btn_close_dm_fragment)
    ImageButton btn_close_dm_fragment;

    @BindView(R.id.btn_show_info_dm)
    ImageButton btn_show_info_dm;

    @BindView(R.id.seekBar_max_zoom)
    SeekBar seekBar_max_zoom;

    @BindView(R.id.btn_reset_dm)
    ImageButton btn_reset_dm;

    @BindView(R.id.btn_grid_dm)
    ImageButton btn_grid_dm;

    @BindView(R.id.btn_save_dm)
    ImageButton btn_save_dm;

    @BindView(R.id.tv_dm_ts)
    TextView tv_dm_ts;

    @BindView(R.id.tv_dm_ts_text)
    TextView tv_dm_ts_text;

    @BindView(R.id.tv_dm_mz_text)
    TextView tv_dm_mz_text;

    @BindView(R.id.tv_dm_cz_text)
    TextView tv_dm_cz_text;

    @BindView(R.id.tv_max_zoom)
    TextView tv_max_zoom;

    int currentMaxZoom = 12;
    int numberSelectedTiles = 0;
    double requiredSpace = 0;
    boolean reachMaxStorage = false;

    ArrayList<TileID> tiles = new ArrayList<>();

    BC_ArcGisController controller;

    private OnDownloadMapFragmentInteractionListener mListener;
    private long availableSpace = 0;
    private int currentGridSize = 7;
    private BCMap map;

    public BCDownloadMapFragment() {
        // Required empty public constructor
    }

    public static BCDownloadMapFragment newInstance(BC_ArcGisController controller) {
        BCDownloadMapFragment fragment = new BCDownloadMapFragment();
        fragment.controller = controller;
        int currentZoom = BC_Helper.getZoomLevelByScale(controller.getMapView().getMapScale());
        if (currentZoom > 11) {
            currentZoom = 11;
        } else if (currentZoom < 7) {
            currentZoom = 7;
        }
        fragment.currentGridSize += (currentZoom % fragment.currentGridSize);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        map = MapUtils.getDefaultOrLastMap();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int minZoom = Math.max(7, Math.max(map.getMinZoom(), map.getMaxZoom() - 8));
            seekBar_max_zoom.setMin(minZoom);
        }
        seekBar_max_zoom.setMax(map.getMaxZoom());
        seekBar_max_zoom.setProgress(Math.min(map.getMaxZoom(), 16));
        currentMaxZoom = Math.min(map.getMaxZoom(), 16);
        tv_max_zoom.setText(String.format("Download detail: %d", currentMaxZoom));
        seekBar_max_zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentMaxZoom = seekBar.getProgress();
                tv_max_zoom.setText(String.format("Download detail: %d", currentMaxZoom));
                calculateSize();
                tv_max_zoom.setText(String.format("Download detail: %d", currentMaxZoom));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tv_dm_ts.setText("0 tile selected");
        tv_dm_ts_text.setText("0.00 MB");
        tv_dm_cz_text.setText(String.valueOf(getZoomLevelByScale(controller.getMapView().getMapScale())));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void calculateSize() {
        numberSelectedTiles = 0;
        requiredSpace = 0;
        availableSpace = BC_Helper.getMegaBytesAvailableInSdCard();
        int factor = 1;
        BCMap lastMap = MapUtils.getDefaultOrLastMap();
        if ("multilayer".equals(lastMap.getMapType())) {
            Gson gson = new Gson();
            List<BCMapLayer> layers = gson.fromJson(lastMap.getMapLayers(), new TypeToken<List<BCMapLayer>>() {
            }.getType());
            factor = layers.size();
        }

        for (TileID tile : this.tiles) {
            numberSelectedTiles++;
            requiredSpace += tile.getRequireSpaceToDownload(tile.getLevel(), seekBar_max_zoom.getProgress()) * factor;
        }

        if (numberSelectedTiles > 0) {
            if (this.tiles.get(0).getLevel() > seekBar_max_zoom.getProgress()) {
                Toast.makeText(getContext(), "Current zoom level is larger that max zoom. Please increase max zoom", Toast.LENGTH_SHORT).show();
            }
        }

        reachMaxStorage = availableSpace < requiredSpace;

        tv_dm_ts_text.setText(String.format("%.2f MB", requiredSpace));

        if (numberSelectedTiles > 0) {
            btn_reset_dm.setImageResource(R.drawable.ic_reset_active);
            if (!reachMaxStorage && requiredSpace > 0) {
                btn_save_dm.setImageResource(R.drawable.ic_download_green);
            } else {
                btn_save_dm.setImageResource(R.drawable.ic_download);
            }
        } else {
            btn_reset_dm.setImageResource(R.drawable.ic_reset);
            btn_save_dm.setImageResource(R.drawable.ic_download);
        }

        if (numberSelectedTiles > 0) {
            tv_dm_ts.setText(String.format("%d tiles selected", numberSelectedTiles));
        } else {
            tv_dm_ts.setText("0 tile selected");
        }

        Log.d(LOG_TAG, String.format("Calculate size of %d tiles: %.2f MB", numberSelectedTiles, requiredSpace));
    }

    @OnClick(R.id.btn_close_dm_fragment)
    public void onCloseClick() {
        if (mListener != null)
            mListener.onClose();
    }

    @OnClick(R.id.btn_show_info_dm)
    public void onShowInfo() {
        if (mListener != null)
            mListener.onShowTutorial();
    }

    @OnClick(R.id.btn_reset_dm)
    public void onReset() {
        if (mListener != null) {
            if (numberSelectedTiles > 0) {
                mListener.onReset();
                tiles.clear();
                numberSelectedTiles = 0;
                calculateSize();
            }
        }
    }

    @OnClick(R.id.btn_grid_dm)
    public void onGridClicked() {
        View v = View.inflate(getContext(), R.layout.layout_grid_number_picker, null);
        final NumberPicker np = v.findViewById(R.id.np_grid_size);
        np.setMaxValue(11);
        np.setMinValue(7);
        np.setValue(currentGridSize);
        np.setWrapSelectorWheel(false);

        BCAlertDialogHelper.buildMapAlertDialog(getActivity(), v, new Runnable() {
            @Override
            public void run() {
                beginDrawGrid(np.getValue());
            }
        }).show();
    }

    @SuppressLint("DefaultLocale")
    private void beginDrawGrid(int cz) {
        currentGridSize = cz;
        if (cz > currentMaxZoom) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                seekBar_max_zoom.setProgress(cz, true);
            } else {
                seekBar_max_zoom.setProgress(cz);
            }
        }
        tv_dm_cz_text.setText(String.format("%d", cz));

        mListener.onToggleDrawGrid(cz);

        if (numberSelectedTiles > 0) {
            tiles.clear();
            numberSelectedTiles = 0;
            calculateSize();
        }
    }

    @OnClick(R.id.btn_save_dm)
    public void onDownloadMapStart() {
        if (correctMembership() == false) {
            String message = String.format("%s requires %s membership to download", map.getMapName(), map.getMembershipType());
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        } else if (reachMaxStorage) {
            Toast.makeText(getContext(), "Not enough space", Toast.LENGTH_LONG).show();
        } else if (numberSelectedTiles == 0) {
            Toast.makeText(getContext(), "No tile selected", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(tv_dm_cz_text.getText().toString()) > Integer.parseInt(tv_dm_mz_text.getText().toString())) {
            Toast.makeText(getContext(), "Current Zoom is larger than max zoom", Toast.LENGTH_LONG).show();
        } else if (numberSelectedTiles > 0 && !reachMaxStorage && mListener != null) {
            BCMap map = MapUtils.getDefaultOrLastMap();
            map.setMapStatus(MAP_STATUS.DOWNLOADED);
            BCMapDBHelper.save(map);
            mListener.onDownloadMapStart(availableSpace, requiredSpace, currentMaxZoom);
        }
    }

    private boolean correctMembership() {
        try {
            BCUser currentUser = BCUtils.getCurrentUser();
            BCMembership current = BCMembershipDataHelper.findbyUserId(currentUser.getUserName());
            BCMembershipType currentType = BCMembershipType.getFromName(current.getMembershipType());
            BCMembershipType require = BCMembershipType.getFromName(map.getMembershipType());
            return require == null || (currentType.ordinal() >= require.ordinal());
        } catch (Exception ex) {
            return false;
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTileTouchToDownloadEvent(BC_OnTileTouchToDownloadEvent event) {
        this.tiles = event.getGgTile();

        calculateSize();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDownloadMapFragmentInteractionListener) {
            mListener = (OnDownloadMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDownloadMapFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        beginDrawGrid(currentGridSize);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public interface OnDownloadMapFragmentInteractionListener {
        void onClose();

        void onShowTutorial();

        void onToggleDrawGrid(int gridSize);

        void onReset();

        void onDownloadMapStart(double availableSpace, double requireSpace, int maxZoom);
    }
}

package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.eventbus.BCSketchEditorChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSketchFragmentInteractionListener}
 * interface.
 */
public class BCSketchControllerFragment extends Fragment {
    public static String TAG = "BCSketchControllerFragment";
    private OnSketchFragmentInteractionListener mListener;
    private ImageButton btn_undo;
    private ImageButton btn_redo;
    private ImageButton btn_save;
    private ImageButton btn_eraser;
    private ImageButton btn_reset;

    private boolean canUndo = false;
    private boolean canRedo = false;
    private boolean canSave = false;

    public BCSketchControllerFragment() {
    }

    public static BCSketchControllerFragment newInstance() {
        return new BCSketchControllerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sketch_controller_list, container, false);

        view.findViewById(R.id.btn_closeSketch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListFragmentInteraction(SketchAction.CLOSE);
            }
        });

        view.findViewById(R.id.btn_color_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(SketchAction.OPEN_COLOR_PICKER);
            }
        });

        btn_undo = view.findViewById(R.id.btn_undo);
        btn_redo = view.findViewById(R.id.btn_redo);
        btn_save = view.findViewById(R.id.btn_save);
        btn_reset = view.findViewById(R.id.btn_reset);
        btn_eraser = view.findViewById(R.id.btn_eraser);

        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canUndo) {
                    mListener.onListFragmentInteraction(SketchAction.UNDO);
                }
            }
        });

        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canRedo) {
                    mListener.onListFragmentInteraction(SketchAction.REDO);
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canSave) {
                    mListener.onListFragmentInteraction(SketchAction.SAVE);
                }
            }
        });

        btn_eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canSave) {
                    mListener.onListFragmentInteraction(SketchAction.ERASER);
                    canUndo = false;
                    canRedo = false;
                    canSave = true;
                    updateUI();
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(SketchAction.RESET);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        if (context instanceof OnSketchFragmentInteractionListener) {
            mListener = (OnSketchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSketchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mListener = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSketchEditorChange(final BCSketchEditorChangeEvent event) {
        canUndo = event.isCanUndo();
        canRedo = event.isCanRedo();
        canSave = event.getTempGeometries().size() > 0 || canUndo;

        updateUI();
    }

    private void updateUI() {
        if (canUndo) {
            btn_undo.setImageResource(R.drawable.ic_undo_active);
        } else {
            btn_undo.setImageResource(R.drawable.ic_undo);
        }

        if (canRedo) {
            btn_redo.setImageResource(R.drawable.ic_redo_active);
        } else {
            btn_redo.setImageResource(R.drawable.ic_redo);
        }

        if (canSave) {
            btn_save.setImageResource(R.drawable.ic_download_green);
        } else {
            btn_save.setImageResource(R.drawable.ic_download);
        }

        if (canSave) {
            btn_eraser.setImageResource(R.drawable.ic_eraser_active);
        } else {
            btn_eraser.setImageResource(R.drawable.ic_eraser);
        }

        if (canSave) {
            btn_reset.setImageResource(R.drawable.ic_reset_active);
        } else {
            btn_reset.setImageResource(R.drawable.ic_reset);
        }
    }

    public enum SketchAction {
        CLOSE,
        OPEN_COLOR_PICKER,
        UNDO,
        SAVE, ERASER, RESET, REDO
    }

    public interface OnSketchFragmentInteractionListener {
        void onListFragmentInteraction(SketchAction action);
    }
}

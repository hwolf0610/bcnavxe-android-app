package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BCColorPickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BCColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BCColorPickerFragment extends Fragment {

    public static String TAG = "BCColorPickerFragment";

    private OnFragmentInteractionListener mListener;

    private String[] colors = new String[]{
            "#ffffff", "#3a8ede", "#000000", "#7c08b1", "#0b9676", "#96850b", "#30334f", "#fbfc00", "#f58220", "#ea20f5"
    };

    public BCColorPickerFragment() {
        // Required empty public constructor
    }

    public static BCColorPickerFragment newInstance() {
        return new BCColorPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GridLayout view = (GridLayout) inflater.inflate(R.layout.fragment_picker, container, false);
        view.animate().translationY(BCUtils.pxFromDp(getContext(), 50)).translationX(BCUtils.pxFromDp(getContext(), 30)).setDuration(0);
        for (final String color : colors) {
            RelativeLayout color1 = (RelativeLayout) inflater.inflate(R.layout.layout_grid_color_item, null);
            ImageButton imb = color1.findViewById(R.id.imb);
            Bitmap bitmap = Bitmap.createBitmap((int) BCUtils.pxFromDp(getContext(), 36), (int) BCUtils.pxFromDp(getContext(), 36), Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.parseColor(color));
            imb.setImageBitmap(bitmap);
            imb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onColorPick(Color.parseColor(color));
                }
            });
            view.addView(color1);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onColorPick(int color);
    }
}

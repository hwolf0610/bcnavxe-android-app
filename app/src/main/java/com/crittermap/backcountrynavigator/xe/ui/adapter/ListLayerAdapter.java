package com.crittermap.backcountrynavigator.xe.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Layer;
import com.esri.arcgisruntime.layers.Layer;

import java.util.ArrayList;

/**
 * Created by nhat@saveondev.com on 12/28/2017.
 */

public class ListLayerAdapter extends ArrayAdapter<BC_Layer> {
    private final Context context;
    private final ArrayList<BC_Layer> values;
    private ListenerCheckBox listenerCheckBox;

    public ListLayerAdapter(Context context, ArrayList<BC_Layer> values, ListenerCheckBox listenerCheckBox) {
        super(context, R.layout.layer_item, values);
        this.context = context;
        this.values = values;
        this.listenerCheckBox = listenerCheckBox;
    }

    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.layer_item, parent, false);
        CheckBox checkBox = rowView.findViewById(R.id.checkBox);
        String text = values.get(position).getLayerType().toString();
        if (values.get(position).getLayer() != null) {
            try {
                Layer l = (Layer) (values.get(position).getLayer());
                text = l.getName();
            } catch (Exception ignored) {

            }
        }
        checkBox.setText(text);
        checkBox.setChecked(values.get(position).getShow());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listenerCheckBox.onRowChecked(position, b);
            }
        });

        return rowView;
    }

    public interface ListenerCheckBox {
        void onRowChecked(int index, boolean value);
    }
}

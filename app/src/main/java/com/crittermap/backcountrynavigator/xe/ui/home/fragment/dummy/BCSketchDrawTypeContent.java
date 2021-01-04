package com.crittermap.backcountrynavigator.xe.ui.home.fragment.dummy;

import com.crittermap.backcountrynavigator.xe.R;

import java.util.ArrayList;
import java.util.List;

public class BCSketchDrawTypeContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<SketchDrawTypeItem> ITEMS = new ArrayList<SketchDrawTypeItem>();

    static {
        addItem(new SketchDrawTypeItem(R.drawable.ic_freehand_polygon, R.drawable.ic_freehand_polygon_green, "Freehand Polygon"));
        addItem(new SketchDrawTypeItem(R.drawable.ic_polygon, R.drawable.ic_polygon_green, "Polygon"));
//        addItem(new SketchDrawTypeItem(R.drawable.ic_account,"Triangle"));
        addItem(new SketchDrawTypeItem(R.drawable.ic_freehand, R.drawable.ic_freehand_green, "Freehand Polyline"));
        addItem(new SketchDrawTypeItem(R.drawable.ic_polyline, R.drawable.ic_polyline_green, "Polyline"));
//        addItem(new SketchDrawTypeItem(R.drawable.ic_account,"Line"));
        addItem(new SketchDrawTypeItem(R.drawable.ic_multipoint, R.drawable.ic_multipoint_green, "Multiple Points"));
        addItem(new SketchDrawTypeItem(R.drawable.ic_point, R.drawable.ic_point_green, "Point"));
    }

    private static void addItem(SketchDrawTypeItem item) {
        ITEMS.add(item);
    }

    public static class SketchDrawTypeItem {
        public final int id;
        public final int idActive;
        public final String content;

        public SketchDrawTypeItem(int id, int idActive, String content) {
            this.id = id;
            this.idActive = idActive;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}

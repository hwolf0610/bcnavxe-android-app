package com.crittermap.backcountrynavigator.xe.controller.utils;

import java.util.ArrayList;

/**
 * Created by nhat@saveondev.com on 12/19/17.
 */

public class MapServer {
    protected static final char[] NUM_CHAR = {'0', '1', '2', '3'};
    final static String[] ABC = {"a", "b", "c"};
    public ArrayList<Integer> maxZoomAlternates = new ArrayList<>();
    String baseUrl = "";
    ArrayList<String> alternateUrls = new ArrayList<>();
    public String getURLforTileID(TileID tid) {
        return "";
    }
}

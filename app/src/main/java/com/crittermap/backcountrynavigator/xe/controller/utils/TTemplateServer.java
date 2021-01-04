package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nhat@saveondev.com on 12/19/17.
 */

public class TTemplateServer extends MapServer {
    private static AtomicInteger SERVER_HASH = new AtomicInteger(0);
    NumberFormat nf = NumberFormat.getInstance(Locale.US);
    NumberFormat ff = NumberFormat.getInstance(Locale.US);
    private AtomicInteger ABC_NUM = new AtomicInteger(0);

    public TTemplateServer() {
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        ff.setMaximumFractionDigits(10);
    }

    public TTemplateServer(String baseUrl) {
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        ff.setMaximumFractionDigits(10);
        this.baseUrl = baseUrl;
    }

    public TTemplateServer(String url, String name, String tag, int minZoom, int maxZoom) {
        nf.setMaximumFractionDigits(0);
        ff.setMaximumFractionDigits(10);
        nf.setGroupingUsed(false);
    }

    /**
     * See: http://msdn.microsoft.com/en-us/library/bb259689.aspx
     *
     * @param zoom  zoom
     * @param tilex tileX
     * @param tiley tileY
     * @return quadtree encoded tile number
     */
    public static String encodeQuadTree(int zoom, int tilex, int tiley) {
        char[] tileNum = new char[zoom];
        for (int i = zoom - 1; i >= 0; i--) {
            // Binary encoding using ones for tilex and twos for tiley. if a bit
            // is set in tilex and tiley we get a three.
            int num = (tilex % 2) | ((tiley % 2) << 1);
            tileNum[i] = NUM_CHAR[num];
            tilex >>= 1;
            tiley >>= 1;
        }
        return new String(tileNum);
    }

    @Override
    public String getURLforTileID(TileID tid) {
        // TODO: 5/28/2018 Handle null baseUrl
        if (baseUrl != null) {
            //replace select characters.
            String result = baseUrl;
            //we may have to replace this if there is an alternate url defined
            for (int i = 0; i < this.alternateUrls.size(); i++) {
                if (this.maxZoomAlternates.get(i) >= tid.level)
                    result = alternateUrls.get(i);
            }
            result = result.replaceFirst("\\{l\\}", Integer.toString(tid.level));
            result = result.replaceFirst("\\{z\\}", Integer.toString(tid.level));
            result = result.replaceFirst("\\{x\\}", Integer.toString(tid.x));
            result = result.replaceFirst("\\{y\\}", Integer.toString(tid.y));

            result = result.replaceFirst("$\\{level\\}", Integer.toString(tid.level));
            result = result.replaceFirst("$\\{col\\}", Integer.toString(tid.x));
            result = result.replaceFirst("$\\{row\\}", Integer.toString(tid.y));

            if (result.contains("{quadtree}"))
                result = result.replaceFirst("\\{quadtree\\}", encodeQuadTree(tid.level, tid.x, tid.y));
            if (result.contains("{nzy}"))
                result = result.replaceFirst("\\{nzy\\}", nzy(tid.level, tid.x, tid.y));
            if (result.contains("{SHASH}")) {
                result = result.replaceFirst("\\{SHASH\\}", Integer.toString(SERVER_HASH.get()));
                SERVER_HASH.set((SERVER_HASH.get() + 1) % 4);
            }

            if (result.contains("SHASHPLUSONE")) {
                result = result.replaceFirst("\\{SHASHPLUSONE\\}", Integer.toString(SERVER_HASH.get() + 1));
                SERVER_HASH.set((SERVER_HASH.get() + 1) % 4);
            }
            if (result.contains("{abc}")) {
                result = result.replaceFirst("\\{abc\\}", ABC[ABC_NUM.get()]);
                ABC_NUM.set((ABC_NUM.get() + 1) % ABC.length);
            }
            if (result.contains("{MBBOX}")) {
                StringBuilder bbox = new StringBuilder(100);
                CoordinateBoundingBox box = BC_GlobalMapTiles.tileBounds(tid);
                bbox.append(nf.format(box.minX));
                bbox.append(',');
                bbox.append(nf.format(box.minY));
                bbox.append(',');
                bbox.append(nf.format(box.maxX));
                bbox.append(',');
                bbox.append(nf.format(box.maxY));
                result = result.replaceFirst("\\{MBBOX\\}", bbox.toString());
                Log.i("TTemplateServer", "MBBOX: " + bbox.toString());
            }

            if (result.contains("{LLBBOX}")) {
//            StringBuilder bbox = new StringBuilder(100);
//            CoordinateBoundingBox box = this.tileResolver.boundingBox(tid);
//            bbox.append(ff.format(box.minlon));
//            bbox.append(',');
//            bbox.append(ff.format(box.maxlat));
//            bbox.append(',');
//            bbox.append(ff.format(box.maxlon));
//            bbox.append(',');
//            bbox.append(ff.format(box.minlat));
//            result = result.replaceFirst("\\{LLBBOX\\}", bbox.toString());
//            Log.i("TTemplateServer", "LLBBOX: " + bbox.toString());
            }
            return result;
        }
        return "";
    }

    private String nzy(int zoom, int x, int y) {
        int nzy = (1 << zoom) - 1 - y;
        return Integer.toString(nzy);
    }

}

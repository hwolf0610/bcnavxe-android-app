package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.util.ArrayList;

/**
 * Created by nhat@saveondev.com on 12/19/17.
 */

public class BC_ConverterUtils {
    @SuppressLint("ObsoleteSdkInt")
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Envelope getEnvelopFromGoogleTile(String centerPoint) {
        centerPoint = centerPoint.trim();
        centerPoint = centerPoint.replace("(", "");
        centerPoint = centerPoint.replace(")", "");
        String[] arr = centerPoint.split(",");
        int level = Integer.parseInt(arr[0]);
        int col = Integer.parseInt(arr[1]);
        int row = Integer.parseInt(arr[2]);
        TileID tileID = new TileID(level, col, row);
        return BC_GlobalMapTiles.tileBoundsAsEnvelop(tileID);
    }

    public static Envelope getEnvelopFromGoogleTile(TileID tileID) {
        return BC_GlobalMapTiles.tileBoundsAsEnvelop(tileID);
    }

    public static Envelope getEnvelopFromString(String fullExtentString) {
        String[] arr = fullExtentString.split(",");
        return new Envelope(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2]), Double.parseDouble(arr[3]), SpatialReferences.getWebMercator());
    }

    public static Envelope getEnvelopFromGoogleTile(int level, int col, int tmsRow) {
        TileID tileID = new TileID(level, col, tmsRow);
        return getEnvelopFromGoogleTile(tileID);
    }

    public static PointCollection getPointCollectionFromEnvelop(Envelope envelope) {
        PointCollection collection = new PointCollection(envelope.getSpatialReference());
        collection.add(new Point(envelope.getXMin(), envelope.getYMin()));
        collection.add(new Point(envelope.getXMin(), envelope.getYMax()));
        collection.add(new Point(envelope.getXMax(), envelope.getYMax()));
        collection.add(new Point(envelope.getXMax(), envelope.getYMin()));

        return collection;
    }

    public static ArrayList<TileID> convertTileIdList(ArrayList<TileID> listChildTile) {
        ArrayList<TileID> l = new ArrayList<>();
        for (TileID t : listChildTile) {
            l.add(BC_GlobalMapTiles.tmsToGoole(t));
        }
        return l;
    }

    public static int tryParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

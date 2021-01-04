package com.crittermap.backcountrynavigator.xe.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

public class SvgBitmapDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private final BitmapPool bitmapPool;

    public SvgBitmapDecoder(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public SvgBitmapDecoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    private Bitmap findBitmap(int width, int height) {
        Bitmap bitmap = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        return bitmap;
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) {
        return false;
    }

    @Nullable
    @Override
    public Resource<Bitmap> decode(@NonNull InputStream source, int width, int height, @NonNull Options options) throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(source);
            if (width == Target.SIZE_ORIGINAL && height == Target.SIZE_ORIGINAL) {
                width = (int) svg.getDocumentWidth();
                height = (int) svg.getDocumentHeight();
                if (width <= 0 || height <= 0) {
                    RectF viewBox = svg.getDocumentViewBox();
                    width = (int) viewBox.width();
                    height = (int) viewBox.height();
                }
            } else {
                if (width == Target.SIZE_ORIGINAL) {
                    width = (int) (height * svg.getDocumentAspectRatio());
                }
                if (height == Target.SIZE_ORIGINAL) {
                    height = (int) (width / svg.getDocumentAspectRatio());
                }
            }
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Either the Target or the SVG document must declare a size.");
            }

            Bitmap bitmap = findBitmap(width, height);
            Canvas canvas = new Canvas(bitmap);
            svg.renderToCanvas(canvas);
            return BitmapResource.obtain(bitmap, bitmapPool);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }
}

package com.crittermap.backcountrynavigator.xe.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.crittermap.backcountrynavigator.xe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henryhai on 3/27/18.
 */

public class BCProcessImageView extends FrameLayout {

    @BindView(R.id.expand_root)
    protected View rootView;

    @BindView(R.id.expand_image_view)
    public ImageView mImageView;

    @BindView(R.id.expand_progress_bar)
    protected ProgressBar mProgressBar;

    public BCProcessImageView(@NonNull Context context) {
        super(context);
        initView();
    }

    public BCProcessImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BCProcessImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BCProcessImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        rootView = inflate(getContext(), R.layout.custom_image_view, this);
        ButterKnife.bind(this, rootView);
    }

    public void setVisibilityProgress(final boolean isVisibility) {
        if (isVisibility) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void setImageViewUrlCircle(String url, int resId) {
        setVisibilityProgress(true);

        RequestBuilder<Drawable> requestBuilder = Glide.with(getContext()).load(url);

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                setVisibilityProgress(false);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                setVisibilityProgress(false);
                return false;
            }
        };

        requestBuilder
                .listener(requestListener)
                .apply(new RequestOptions()
                        .placeholder(resId)
                        .transform(new CircleCrop()))
                .into(mImageView);
    }

    public void setImageViewBitmapCircle(Bitmap bitmap, int resId) {
        setVisibilityProgress(true);

        RequestBuilder<Drawable> requestBuilder = Glide.with(getContext()).load(bitmap);

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                setVisibilityProgress(false);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                setVisibilityProgress(false);
                return false;
            }
        };

        requestBuilder
                .listener(requestListener)
                .apply(new RequestOptions()
                        .placeholder(resId)
                        .transform(new MultiTransformation<>(new CircleCrop(), new FitCenter())))
                .into(mImageView);
    }

    public void setImageViewUrlWithOutCropCircle(String url, int resId) {
        setVisibilityProgress(true);

        RequestBuilder<Drawable> requestBuilder = Glide.with(getContext()).load(url);

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                setVisibilityProgress(false);
                return false;
            }
        };

        requestBuilder
                .listener(requestListener)
                .apply(new RequestOptions()
                        .placeholder(resId))
                .into(mImageView);
    }

    public void setImageViewUrlWithCropCenter(String url, int resId) {
        setVisibilityProgress(true);

        RequestBuilder<Drawable> requestBuilder = Glide.with(getContext()).load(url);

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                setVisibilityProgress(false);
                return false;
            }
        };

        requestBuilder
                .listener(requestListener)
                .apply(new RequestOptions()
                        .placeholder(resId)
                        .transform(new CenterCrop()))
                .into(mImageView);
    }

    public void setImageResource(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setImageResourceDrawable(int drawableId) {
        mImageView.setBackgroundResource(drawableId);
    }

    public void setImageCircle(Uri uri) {
        setImageViewUrlCircle(uri.getPath(), R.drawable.ic_default_image);
    }

    public void setOrignalImageUrl(String url) {
        mImageView.setTag(1, url);
    }

    public String getOrinalImageUrl() {
        return mImageView.getTag(1).toString();
    }
}

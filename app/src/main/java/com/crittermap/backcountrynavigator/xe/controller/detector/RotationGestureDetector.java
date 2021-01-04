package com.crittermap.backcountrynavigator.xe.controller.detector;

import android.view.MotionEvent;
import android.view.View;

public class RotationGestureDetector {
    private float realAngle;
    private float prevRealAngle;
    private float mAngle;
    private OnRotationGestureListener mListener;
    private float mPrevAngle;

    public RotationGestureDetector(OnRotationGestureListener listener) {
        mListener = listener;
    }

    public float getAngle() {
        return mAngle;
    }

    public float getPrevAngle() {
        return mPrevAngle;
    }

    public float getRealAngle() {
        return realAngle;
    }

    public float getPrevRealAngle() {
        return prevRealAngle;
    }

    public boolean onTouchEvent(final View v, MotionEvent event) {
        final float xc = v.getWidth() / 2;
        final float yc = v.getHeight() / 2;

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                v.clearAnimation();
                mAngle = (float) Math.toDegrees(Math.atan2(x - xc, yc - y));
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mPrevAngle = mAngle;
                mAngle = (float) Math.toDegrees(Math.atan2(x - xc, yc - y));
                prevRealAngle = realAngle;
                realAngle = realAngle + mAngle - mPrevAngle;
                mListener.OnRotation(this);
                break;
            }
            case MotionEvent.ACTION_UP: {
                mPrevAngle = mAngle = 0;
                break;
            }
        }
        return true;
    }

    public interface OnRotationGestureListener {
        void OnRotation(RotationGestureDetector rotationDetector);
    }
}

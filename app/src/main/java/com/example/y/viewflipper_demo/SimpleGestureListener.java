package com.example.y.viewflipper_demo;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int FLING_MIN_DISTANCE = 100;
    private static final int FLING_MIN_VELOCITY = 200;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

        }



        return super.onFling(e1, e2, velocityX, velocityY);
    }
}

package com.example.auser.zthacker.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class FixedSpeedScroller extends Scroller {
    private int mDuration = 1500;
 
    public FixedSpeedScroller(Context context) {
        super(context);
    }
 
    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed activity_settled instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
 
    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed activity_settled instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
 
    public void setmDuration(int time) {
        mDuration = time;
    }
 
    public int getmDuration() {
        return mDuration;
    }
}


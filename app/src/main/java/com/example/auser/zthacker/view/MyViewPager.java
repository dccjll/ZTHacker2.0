package com.example.auser.zthacker.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;

import java.lang.reflect.Field;

/**
 * Created by zhengkq on 2016/12/23.
 */

public class MyViewPager extends ViewPager {
    private boolean noScroll=false;

    public MyViewPager(Context context){
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 禁止左右滑动
     * */
    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
    /**
     * 禁止滑动动画效果
     * */
    public void SetNomScroller(Boolean bol){
        if(bol){
            try {
                Field mField = ViewPager.class.getDeclaredField("mScroller");
                mField.setAccessible(true);
                FixedSpeedScroller mScroller = new FixedSpeedScroller(this.getContext(),new AccelerateInterpolator());
                mField.set(this, mScroller);
                mScroller.setmDuration(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
	        /* return false;//super.onTouchEvent(arg0); */
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}

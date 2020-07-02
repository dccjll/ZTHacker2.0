package com.example.auser.zthacker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.auser.zthacker.view.pullableview.PullableScrollView;

/**
 * Created by zhengkq on 2017/8/23.
 */

public class ObservableScrollView extends ScrollView{
    private ScrollViewListener scrollViewListener = null;
    private int lastX;
    private int lastY;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = (int)ev.getX();
                lastY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int)ev.getX();
                int y = (int)ev.getY();
                if (Math.abs(y-lastY)<Math.abs(x-lastX)){
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
				/*int x = (int)ev.getX();
				int y = (int)ev.getY();
				if (Math.abs(y-lastY)>Math.abs(x-lastX)){
					return true;
				}*/
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}

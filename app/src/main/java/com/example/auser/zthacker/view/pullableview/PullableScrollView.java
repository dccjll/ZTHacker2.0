package com.example.auser.zthacker.view.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.auser.zthacker.view.ObservableScrollView;

public class PullableScrollView extends ScrollView implements Pullable
{

	private int lastX;
	private int lastY;
	private ScrollViewListener scrollViewListener = null;

	public PullableScrollView(Context context)
	{
		super(context);

	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return true;
		else
			return false;
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
		void onScrollChanged(PullableScrollView scrollView, int x, int y, int oldx, int oldy);

	}
}

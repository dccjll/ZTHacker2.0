package com.example.auser.zthacker.view.pullableview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by zhengkq on 2017/8/14.
 */

public class PullableRecycleView extends RecyclerView implements Pullable{

    private int firstVisibleItem;
    private int lastVisibleItemPosition;

    public PullableRecycleView(Context context) {
        super(context);
    }
    public PullableRecycleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableRecycleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (getLayoutManager() instanceof FullyLinearLayoutManager) {
            firstVisibleItem = ((FullyLinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        }
        if (getChildCount() == 0) {
            return true;
        } else if (firstVisibleItem == 0
                &&getChildAt(0)!=null&& getChildAt(0).getTop() >= 0) {
            // 滑到ListView的顶部了
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean canPullUp()
    {
        if (getLayoutManager() instanceof FullyLinearLayoutManager) {
            firstVisibleItem = ((FullyLinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            lastVisibleItemPosition = ((FullyLinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            lastVisibleItemPosition = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        }
        if (getChildCount() == 0)
        {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (lastVisibleItemPosition==getChildCount()-1){

            // 滑到底部了
            if (getChildAt(lastVisibleItemPosition - firstVisibleItem) != null
                    && getChildAt(lastVisibleItemPosition - firstVisibleItem)
                    .getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }

   /*@Override
    public boolean canPullUp() {
        return false;
    }*/
}

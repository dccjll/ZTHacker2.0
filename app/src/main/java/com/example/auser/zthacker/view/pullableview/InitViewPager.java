package com.example.auser.zthacker.view.pullableview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by zhengkq on 2017/5/4.
 */

public class InitViewPager extends ViewPager {
    public InitViewPager(Context context) {
        super(context);
    }

    public InitViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, false);//<span style="color: rgb(70, 70, 70); font-family: 'Microsoft YaHei', 'Helvetica Neue', SimSun; font-size: 14px; line-height: 21px; background-color: rgb(188, 211, 229);">表示切换的时候，不需要切换时间。</span>
    }
}

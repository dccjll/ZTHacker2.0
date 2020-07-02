package com.example.auser.zthacker.ui.activity.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by zhengkq on 2017-11-21.
 */

public class CourseRecommendActivity extends BaseActivity{
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.videoplayer)
    JCVideoPlayerStandard videoplayer;
    @BindView(R.id.banner_pictures)
    BGABanner banner_pictures;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.swipe_target)
    RecyclerView swipe_target;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_recommend);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }
}

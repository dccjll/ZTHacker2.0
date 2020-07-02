package com.example.auser.zthacker.ui.activity.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.TeachingResourseInfoBean;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import java.io.Serializable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import de.greenrobot.event.EventBus;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by zhengkq on 2018-1-26.
 */

public class CourseAndResourseActivity extends BaseActivity{
    @BindView(R.id.videoplayer)
    JCVideoPlayerStandard videoplayer;
    @BindView(R.id.tv_teaching_resourse)
    TextView tv_teaching_resourse;
    private TeachingResourseInfoBean teachingResourseInfo;
    private VideoFrameImageLoader videoFrameImageLoader;

    public static void startActivity(Context context,TeachingResourseInfoBean teachingResourseInfo){
        Intent intent = new Intent(context,CourseAndResourseActivity.class);
        intent.putExtra("teachingResourseInfo",(Serializable) teachingResourseInfo);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_and_resourse);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        Intent intent = getIntent();
        teachingResourseInfo = (TeachingResourseInfoBean) intent.getSerializableExtra("teachingResourseInfo");
        setCentreText(teachingResourseInfo.getFileName());
        videoplayer.setUp(teachingResourseInfo.getFileUrl(), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
        videoplayer.backButton.setVisibility(View.INVISIBLE);
        videoplayer.battery_level.setVisibility(View.INVISIBLE);
        videoplayer.batteryTimeLayout.setVisibility(View.INVISIBLE);
        videoplayer.titleTextView.setVisibility(View.GONE);
        videoplayer.clarity.setVisibility(View.GONE);
        //videoplayer.tv_total_time.setVisibility(View.GONE);
        videoFrameImageLoader = new VideoFrameImageLoader(this,null);
        //从缓存中获取图片
        Bitmap bitmap = videoFrameImageLoader.showCacheBitmap(VideoFrameImageLoader.formatVideoUrl(teachingResourseInfo.getFileUrl()));
        String time = videoFrameImageLoader.getStringFormMemCache(teachingResourseInfo.getFileUrl());
        if (time != null) {
            videoplayer.thumbImageView.setImageBitmap(bitmap);
            videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(time)));
        } else {
            //没有从缓存中加载到时，先设置一张默认图
            videoplayer.thumbImageView.setImageResource(R.mipmap.img_placeholder_152);
            videoFrameImageLoader.cutVideoFrameImage(teachingResourseInfo.getFileUrl(), new VideoFrameImageLoader.OnFrameImageLoaderListener() {
                @Override
                public void onImageLoader(UtilBean utilBean, String url) {
                    if (videoplayer != null && utilBean != null) {
                        videoplayer.thumbImageView.setImageBitmap(utilBean.getBitmap());
                        videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(utilBean.getaString())));
                    }
                }
            });
        }
        tv_teaching_resourse.setText("查看课件");
    }

    @OnClick({R.id.image_back,R.id.tv_teaching_resourse})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_teaching_resourse:
                String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!EasyPermissions.hasPermissions(CourseAndResourseActivity.this, perms)) {
                    EasyPermissions.requestPermissions(CourseAndResourseActivity.this, "需要访问手机存储权限！", 10086, perms);
                } else {
                    WpsDisplayActivity.startActivity(this, teachingResourseInfo.getCourseWarePPTUrl()
                            , teachingResourseInfo.getCourseWareName());
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayerStandard.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

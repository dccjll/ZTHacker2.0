package com.example.auser.zthacker.ui.activity.course;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyFragmentPagerAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;
import com.example.auser.zthacker.ui.fragment.main.course.CourseChildFragment;
import com.example.auser.zthacker.view.MyViewPager;
import com.example.auser.zthacker.zxing.activity.CaptureActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/24.
 */

public class CourseInfoIntroActivity extends BaseActivity{
    @BindView(R.id.tv_video)
    TextView tv_video;
    @BindView(R.id.tv_course_resourse)
    TextView tv_course_resourse;
    @BindView(R.id.tv_teaching_material)
    TextView tv_teaching_material;
    @BindView(R.id.tv_teaching_tool)
    TextView tv_teaching_tool;
    @BindView(R.id.iv_bottom1)
    ImageView iv_bottom1;
    @BindView(R.id.iv_bottom2)
    ImageView iv_bottom2;
    @BindView(R.id.iv_bottom3)
    ImageView iv_bottom3;
    @BindView(R.id.iv_bottom4)
    ImageView iv_bottom4;
    @BindView(R.id.viewPager_course)
    MyViewPager viewPager_course;
    private List<android.support.v4.app.Fragment> fragmentList;
    private MyFragmentPagerAdapter adapter;
    private int currentItem;
    private String url;
    public HomeClassTypeInfoBean courseInfoBean;

    public static void startActivity(Context context,String url,HomeClassTypeInfoBean courseInfoBean){
        Intent intent = new Intent(context,CourseInfoIntroActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("courseInfoBean",(Serializable) courseInfoBean);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info_intro);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        url = getIntent().getStringExtra("url");
        courseInfoBean = (HomeClassTypeInfoBean) getIntent().getSerializableExtra("courseInfoBean");
        initFragment();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(CourseChildFragment.newInstance("1"));
        fragmentList.add(CourseChildFragment.newInstance("2"));
        fragmentList.add(CourseChildFragment.newInstance(courseInfoBean.getMaterialLink()));
        fragmentList.add(CourseChildFragment.newInstance(courseInfoBean.getAidsLink()));
        initAdapter();
    }

    private void initAdapter() {
        if (adapter==null){
            adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
            viewPager_course.setAdapter(adapter);
            viewPager_course.SetNomScroller(true);
            viewPager_course.setNoScroll(true);
            viewPager_course.setOffscreenPageLimit(3);
        }
        if (url.equals("1")){
            currentItem=0;
            setTab(tv_video,iv_bottom1);
        }else if (url.equals("2")){
            currentItem=1;
            setTab(tv_course_resourse,iv_bottom2);
        }else if (url.equals("3")){
            currentItem=2;
            setTab(tv_teaching_material,iv_bottom3);
        } else if (url.equals("4")){
            currentItem=3;
            setTab(tv_teaching_tool,iv_bottom4);
        }else {
            currentItem=0;
            setTab(tv_video,iv_bottom1);
        }
        viewPager_course.setCurrentItem(currentItem);
    }

    @OnClick({R.id.image_back,R.id.rl_video,R.id.rl_course_resourse,R.id.rl_teaching_material,R.id.rl_teaching_tool,R.id.iv_scan,
              R.id.iv_contact_line})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rl_video:
                currentItem = 0;
                setTab(tv_video,iv_bottom1);
                break;
            case R.id.rl_course_resourse:
                currentItem = 1;
                setTab(tv_course_resourse,iv_bottom2);
                break;
            case R.id.rl_teaching_material:
                currentItem = 2;
                setTab(tv_teaching_material,iv_bottom3);
                break;
            case R.id.rl_teaching_tool:
                currentItem = 3;
                setTab(tv_teaching_tool,iv_bottom4);
                break;
            case R.id.iv_scan:
                CaptureActivity.startActivity(this);
                break;
            case R.id.iv_contact_line:
                ClassConsultActivity.startActivity(CourseInfoIntroActivity.this, Config.CLASS_CONSULT);
                break;
        }
    }

    private void setTab(TextView text, ImageView iv_bottom) {
        tv_video.setTextColor(getResources().getColor(R.color.text_64));
        iv_bottom1.setVisibility(View.INVISIBLE);

        tv_course_resourse.setTextColor(getResources().getColor(R.color.text_64));
        iv_bottom2.setVisibility(View.INVISIBLE);

        tv_teaching_material.setTextColor(getResources().getColor(R.color.text_64));
        iv_bottom3.setVisibility(View.INVISIBLE);

        tv_teaching_tool.setTextColor(getResources().getColor(R.color.text_64));
        iv_bottom4.setVisibility(View.INVISIBLE);

        text.setTextColor(getResources().getColor(R.color.text_32));
        iv_bottom.setVisibility(View.VISIBLE);
        viewPager_course.setCurrentItem(currentItem);
    }
}

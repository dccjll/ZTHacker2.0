package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyFragmentPagerAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.ui.fragment.main.course.MineCourseChildFragment;
import com.example.auser.zthacker.view.MyViewPager;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/16.
 */

public class VideoManagerListActivity extends BaseActivity {
    @BindView(R.id.tv_video)
    TextView tv_video;
    @BindView(R.id.tv_course_resourse)
    TextView tv_course_resourse;
    @BindView(R.id.iv_bottom1)
    ImageView iv_bottom1;
    @BindView(R.id.iv_bottom2)
    ImageView iv_bottom2;
    @BindView(R.id.viewPager_course)
    MyViewPager viewPager_course;
    private List<android.support.v4.app.Fragment> fragmentList;
    private MyFragmentPagerAdapter adapter;
    private int currentItem;
    public String courseId;

    public static void startActivity(Context context,String courseId){
        Intent intent = new Intent(context,VideoManagerListActivity.class);
        intent.putExtra("courseId",courseId);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_manager_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
       /* setTop(R.color.black);
        intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();*/
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        setTop(R.color.black);
        initFragment();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(MineCourseChildFragment.newInstance(Config.VIDEO));
        fragmentList.add(MineCourseChildFragment.newInstance(Config.VIDEO_ABOUT_TEACHING_RESOURSE));
        initAdapter();
    }

    private void initAdapter() {
        if (adapter==null){
            adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
            viewPager_course.setAdapter(adapter);
            viewPager_course.SetNomScroller(true);
            viewPager_course.setNoScroll(true);
            viewPager_course.setOffscreenPageLimit(1);
        }
        currentItem=0;
        setTab(tv_video,iv_bottom1);
        viewPager_course.setCurrentItem(currentItem);
    }

    @OnClick({R.id.image_back,R.id.rl_video,R.id.rl_course_resourse})
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
        }
    }

    private void setTab(TextView text, ImageView iv_bottom) {
        tv_video.setTextColor(getResources().getColor(R.color.text_64));
        iv_bottom1.setVisibility(View.INVISIBLE);

        tv_course_resourse.setTextColor(getResources().getColor(R.color.text_64));
        iv_bottom2.setVisibility(View.INVISIBLE);

        text.setTextColor(getResources().getColor(R.color.text_32));
        iv_bottom.setVisibility(View.VISIBLE);
        viewPager_course.setCurrentItem(currentItem);
    }

    /*private void initRequest() {
        if (!TextUtil.isNull(courseId)){
            ApiRequestData.getInstance(this).ShowDialog(null);
            OkGo.post(ApiRequestData.getInstance(this).MineMyClassVideoList)
                    .tag(this)
                    .params("courseId",courseId)
                    .params("type","1")
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (gsonBuilder==null){
                                gsonBuilder = new GsonBuilder();
                            }
                            ApiRequestData.getInstance(VideoManagerListActivity.this).getDialogDismiss();
                            mData = gsonBuilder
                                    .setPrettyPrinting()
                                    .disableHtmlEscaping()
                                    .create().fromJson(s, new TypeToken<NormalObjData<List<VideoInfoBean>>>(){}.getType());
                            code = mData.getCode();
                            list = mData.getData();
                            if (!TextUtil.isNull(code)&&code.equals("0")){
                                ToastUtil.show(VideoManagerListActivity.this, mData.getMsg());
                                return;
                            }
                            if (list!=null){
                                initAdapter();
                            }
                        }
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ApiRequestData.getInstance(VideoManagerListActivity.this).getDialogDismiss();
                            ToastUtil.show(VideoManagerListActivity.this,e.getMessage());
                        }
                    });
        }
    }*/

   /* private void initAdapter() {
        if (adapter==null){
            adapter = new VideoListAdapter(this,list);
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }*/
}

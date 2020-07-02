package com.example.auser.zthacker.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyFragmentPagerAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.fragment.main.main.CourseFragment;
import com.example.auser.zthacker.ui.fragment.main.main.FoundFragment;
import com.example.auser.zthacker.ui.fragment.main.main.HomeFragment;
import com.example.auser.zthacker.ui.fragment.main.main.MineFragment;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.MyViewPager;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.viewPager_main)
    MyViewPager viewPager_main;
    @BindView(R.id.ll_tab1)
    LinearLayout ll_tab1;
    @BindView(R.id.image_tab1)
    ImageView image_tab1;
    @BindView(R.id.textView_tab1)
    TextView textView_tab1;
    @BindView(R.id.ll_tab2)
    LinearLayout ll_tab2;
    @BindView(R.id.image_tab2)
    ImageView image_tab2;
    @BindView(R.id.textView_tab2)
    TextView textView_tab2;
    /*@BindView(R.id.ll_tab3)
    LinearLayout ll_tab3;
    @BindView(R.id.image_tab3)
    ImageView image_tab3;
    @BindView(R.id.textView_tab3)
    TextView textView_tab3;*/
    @BindView(R.id.ll_tab4)
    LinearLayout ll_tab4;
    @BindView(R.id.image_tab4)
    ImageView image_tab4;
    @BindView(R.id.textView_tab4)
    TextView textView_tab4;
    private List<Fragment> fragmentList;
    private int currentItem;
    private MyFragmentPagerAdapter adapter;
    private String userId;
    private GsonBuilder gsonBuilder;
    public AppPersonalInfoBean personalInfo;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        userId = SPUtils.getSharedStringData(this, "userId");
        if (TextUtil.isNull(userId)){//判断是否登录
            initFragment();
        }else {
            ApiRequestData.getInstance(this).ShowDialog(null);
            initRequest();
        }
    }

    private void initRequest() {
        OkGo.post(ApiRequestData.getInstance(this).MineInfo)// 请求方式和请求url
                .tag(this)
                .params("id", userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(MainActivity.this).getDialogDismiss();
                        NormalObjData<AppPersonalInfoBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<AppPersonalInfoBean>>() {
                                }.getType());
                        personalInfo = mData.getData();
                        String code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(MainActivity.this, mData.getMsg());
                            return;
                        }
                        initFragment();
                        SPUtils.setSharedStringData(MainActivity.this, "phone", personalInfo.getPhoneNumber());
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ApiRequestData.getInstance(MainActivity.this).getDialogDismiss();
                        ToastUtil.show(MainActivity.this, e.getMessage());
                        initFragment();
                    }
                });
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new CourseFragment());
        fragmentList.add(new FoundFragment());
        //fragmentList.add(new CommunityFragment());
        fragmentList.add(new MineFragment());
        initAdapter();
    }

    private void initAdapter() {
        if (adapter==null){
            adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
            viewPager_main.setAdapter(adapter);
            viewPager_main.SetNomScroller(true);
            viewPager_main.setNoScroll(true);
            viewPager_main.setOffscreenPageLimit(3);
        }
        viewPager_main.setCurrentItem(currentItem);
        viewPager_main.setOnPageChangeListener(this);
    }

    @OnClick({R.id.ll_tab1,R.id.ll_tab2/*,R.id.ll_tab3*/,R.id.ll_tab4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_tab1:
                currentItem = 0;
                setOneSelect();
                viewPager_main.setCurrentItem(currentItem);
                break;
            case R.id.ll_tab2:
                currentItem = 1;
                setTwoSelect();
                viewPager_main.setCurrentItem(currentItem);
                break;
            /*case R.id.ll_tab3:
                currentItem = 2;
                setThreeSelect();
                viewPager_main.setCurrentItem(currentItem);
                break;*/
            case R.id.ll_tab4:
                currentItem = 3;
                setFourSelect();
                viewPager_main.setCurrentItem(currentItem);
                break;
        }
    }

    private void setOneSelect() {
        image_tab1.setImageResource(R.mipmap.icon_home_hl);
        textView_tab1.setTextColor(getResources().getColor(R.color.send_captcha_bg));
        image_tab2.setImageResource(R.mipmap.icon_course);
        textView_tab2.setTextColor(getResources().getColor(R.color.text_64));
        /*image_tab3.setImageResource(R.mipmap.icon_find);
        textView_tab3.setTextColor(getResources().getColor(R.color.text_64));*/
        image_tab4.setImageResource(R.mipmap.icon_mine);
        textView_tab4.setTextColor(getResources().getColor(R.color.text_64));
    }

    private void setTwoSelect() {
        image_tab1.setImageResource(R.mipmap.icon_home);
        textView_tab1.setTextColor(getResources().getColor(R.color.text_64));
        image_tab2.setImageResource(R.mipmap.icon_course_hl);
        textView_tab2.setTextColor(getResources().getColor(R.color.send_captcha_bg));
        /*image_tab3.setImageResource(R.mipmap.icon_find);
        textView_tab3.setTextColor(getResources().getColor(R.color.text_64));*/
        image_tab4.setImageResource(R.mipmap.icon_mine);
        textView_tab4.setTextColor(getResources().getColor(R.color.text_64));
    }

    private void setThreeSelect() {
        image_tab1.setImageResource(R.mipmap.icon_home);
        textView_tab1.setTextColor(getResources().getColor(R.color.text_64));
        image_tab2.setImageResource(R.mipmap.icon_course);
        textView_tab2.setTextColor(getResources().getColor(R.color.text_64));
        /*image_tab3.setImageResource(R.mipmap.icon_find_hl);
        textView_tab3.setTextColor(getResources().getColor(R.color.send_captcha_bg));*/
        image_tab4.setImageResource(R.mipmap.icon_mine);
        textView_tab4.setTextColor(getResources().getColor(R.color.text_64));
    }

    private void setFourSelect() {
        image_tab1.setImageResource(R.mipmap.icon_home);
        textView_tab1.setTextColor(getResources().getColor(R.color.text_64));
        image_tab2.setImageResource(R.mipmap.icon_course);
        textView_tab2.setTextColor(getResources().getColor(R.color.text_64));
        /*image_tab3.setImageResource(R.mipmap.icon_find);
        textView_tab3.setTextColor(getResources().getColor(R.color.text_64));*/
        image_tab4.setImageResource(R.mipmap.icon_mine_hl);
        textView_tab4.setTextColor(getResources().getColor(R.color.send_captcha_bg));
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Subscribe
    public void onEventMainThread(String msg) {
        if (msg.equals(Config.MAIN_COURSE)){
            currentItem = 1;
            setTwoSelect();
            viewPager_main.setCurrentItem(currentItem);
        }else if (msg.equals(Config.MAIN_FOUND_COMMUNITY)||msg.equals(Config.MAIN_FOUND_NEWS)){
            currentItem = 2;
            setThreeSelect();
            viewPager_main.setCurrentItem(currentItem);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private long firstClick;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondClick = System.currentTimeMillis();
            if (secondClick - firstClick > 1500) {
                Toast.makeText(MainActivity.this, "再次点击退出", Toast.LENGTH_SHORT).show();
                firstClick = secondClick;
            } else {
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}

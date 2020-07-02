package com.example.auser.zthacker.ui.fragment.main.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.CourseAdapter;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.BannerInfoBean;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.bean.MainCourseBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.home.ConceptActivity;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Auser on 2017/8/4.
 */

public class CourseFragment extends BaseFragment implements OnRefreshListener{
    @BindView(R.id.banner_home)
    BGABanner banner_home;
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    boolean isRefresh = false;
    private GsonBuilder gsonBuilder;
    private MainCourseBean courseData;
    private CourseAdapter adapter;
    private String code;
    private List<BannerInfoBean> bannerInfoManage;
    private List<HomeClassTypeInfoBean> courseManage = new ArrayList<>();
    private int overrideWidth, overrideHeight;
    private float sizeMultiplier = 0.5f;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_found_community);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitFinished = true;
    }

    protected void initView() {
        setTop(R.color.black);
        initAdapter();
    }

    private void initAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setLoadMoreEnabled(false);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        setCourseData();
        //((MainActivity)getActivity()).ApiRequestData.getInstance(this).ShowDialog(null);
        //initData();
    }

    private void initData() {
        OkGo.get(ApiRequestData.getInstance(getActivity()).MainCourseData)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        if (isRefresh){
                            swipe_layout.setRefreshing(false);
                        }
                        if(!isFirst) {
                            isFirst = true;
                        }
                        NormalObjData<MainCourseBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<MainCourseBean>>(){}.getType());
                        courseData = mData.getData();
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        code = mData.getCode();
                        if (!TextUtil.isNull(code)&& code.equals("0")){
                            ToastUtil.show(getActivity(),mData.getMsg());
                            return;
                        }
                        bannerInfoManage = courseData.getBannerInfoManage();
                        if (courseManage!=null){
                            courseManage.clear();
                        }
                        courseManage.addAll(courseData.getCourseManage());
                        if (bannerInfoManage!=null){
                            setBannerData();
                        }
                        if (courseManage!=null){
                            setCourseData();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        if (isRefresh){
                            swipe_layout.setRefreshing(false);
                        }
                    }
                });
    }

    private void setCourseData() {
        //设置适配器
        if (adapter==null){
            adapter = new CourseAdapter(getActivity(),courseManage);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void setBannerData() {
        if(bannerInfoManage.size()<=1){
            banner_home.setAutoPlayAble(false);
        }else {
            banner_home.setAutoPlayAble(true);
        }
        banner_home.setData(bannerInfoManage,null);
        banner_home.setAdapter(new BGABanner.Adapter<ImageView,BannerInfoBean>(){
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, BannerInfoBean model, int position) {
                if (!TextUtil.isNull(model.getPicUrl())){
                   /* if (overrideWidth <= 0 && overrideHeight <= 0) {
                        options.sizeMultiplier(sizeMultiplier);
                    } else {
                        options.override(overrideWidth, overrideHeight);
                    }*/
                    Glide.with(getActivity())
                            .load(model.getPicUrl())
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .fitCenter()
                            .placeholder(R.mipmap.img_placeholder_320)
                            .override(490,240)
                            .thumbnail(0.2f)
                            .into(itemView);
                }
            }
        });
        banner_home.setDelegate(new BGABanner.Delegate<ImageView,BannerInfoBean>(){
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, BannerInfoBean model, int position) {
                if (!TextUtil.isNull(model.getUrl())){
                    ConceptActivity.startActivity(getActivity(),model.getUrl(),model.getBannerTitle(),1);
                }
            }
        });
    }

    @OnClick({R.id.iv_contact_line})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_contact_line:
                ClassConsultActivity.startActivity(getActivity(), Config.CLASS_CONSULT);
                break;
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
            initData();
        } else {
            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }
}

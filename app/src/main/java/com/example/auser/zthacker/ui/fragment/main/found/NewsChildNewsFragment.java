package com.example.auser.zthacker.ui.fragment.main.found;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundNewsAdapter;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.BannerInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MainFoundBean;
import com.example.auser.zthacker.bean.NewsInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.NewsInfoActivity;
import com.example.auser.zthacker.ui.activity.home.ConceptActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.bingoogolapple.bgabanner.BGABanner;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/15.
 */

public class NewsChildNewsFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.banner_home)
    BGABanner banner_home;
    @BindView(R.id.iv_contact_line)
    ImageView iv_contact_line;
    @BindView(R.id.imageView_top)
    ImageView imageView_top;
    boolean isRefresh = false;
    private FoundNewsAdapter adapter;
    private GsonBuilder gsonBuilder;
    private String code;
    private MainFoundBean mainFoundBean;
    private List<BannerInfoBean> bannerInfoManage = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    private List<NewsInfoBean> newsList = new ArrayList<>();
    private String userId;
    private int indexfrom;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_found_community);
        EventBus.getDefault().register(this);
        Glide.get(getActivity()).setMemoryCategory(MemoryCategory.HIGH);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitFinished = true;
    }

    protected void initView() {
        userId = SPUtils.getSharedStringData(getActivity(), "userId");
        iv_contact_line.setVisibility(View.GONE);
        imageView_top.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(getActivity(), RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        recyclerView.addItemDecoration(recycleViewDividerLine);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        mVideoFrameImageLoader = new VideoFrameImageLoader(getActivity(), recyclerView);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
    }

    private void initRequest() {
        OkGo.get(ApiRequestData.getInstance(getActivity()).MainFoundData)
                .tag(this)
                .params("sysAppUser",userId)
                .params("dataIndex",indexfrom)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        initRefresh();
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        if(!isFirst) {
                            isFirst = true;
                        }
                        NormalObjData<MainFoundBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<MainFoundBean>>() {
                                }.getType());
                        code = mData.getCode();
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(getActivity(), mData.getMsg());
                            return;
                        }
                        mainFoundBean = mData.getData();
                        if (bannerInfoManage!=null){
                            bannerInfoManage.clear();
                        }
                        if (mainFoundBean.getBannerInfoManage()!=null&&mainFoundBean.getBannerInfoManage().size()>0){
                            bannerInfoManage.addAll(mainFoundBean.getBannerInfoManage());
                        }
                        if (mainFoundBean.getPublishInfo()==null||mainFoundBean.getPublishInfo().size()==0){
                            ToastUtil.show(getActivity(),"没有更多了！");
                            return;
                        }
                        newsList.addAll(mainFoundBean.getPublishInfo());
                        if (bannerInfoManage != null && bannerInfoManage.size() > 0) {
                            setBanner();
                        }
                        if (newsList != null) {
                            setNewList();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(getActivity(), e.getMessage());
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        initRefresh();
                    }
                });
    }

    private void setNewList() {
        if (videoUrlList!=null&&videoUrlList.size()>0){
            videoUrlList.clear();
        }
        for (int i = 0;i<newsList.size();i++){
            if (!TextUtil.isNull(newsList.get(i).getShowType())&&newsList.get(i).getShowType().equals("1")){
                videoUrlList.add(newsList.get(i).getAttachList().get(0).getFileUrl());
            }else {
                videoUrlList.add("");
            }
        }
        String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
        mVideoFrameImageLoader.setVideoUrls(videoArray);
        Log.e("videoArray",videoArray.length+"");
        if (adapter == null) {
            adapter = new FoundNewsAdapter(getActivity(), newsList,2);
            adapter.setVideoUrls(mVideoFrameImageLoader);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new FoundNewsAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (newsList.get(position).getShowType().equals("1")){
                        return;
                    }
                    NewsInfoActivity.startActivity(getActivity(),newsList.get(position));
                }
            });
            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {

                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {
                    if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                        JCVideoPlayer videoPlayer = (JCVideoPlayer) JCVideoPlayerManager.getCurrentJcvd();
                        if (videoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                            videoPlayer.release();
                        }
                    }
                }
            });
        } else {
            adapter.setVideoUrls(mVideoFrameImageLoader);
            adapter.notifyDataSetChanged();
        }
    }

    private void setBanner() {
        if(bannerInfoManage.size()<=1){
            banner_home.setAutoPlayAble(false);
        }else {
            banner_home.setAutoPlayAble(true);
        }
        banner_home.setData(bannerInfoManage, null);
        banner_home.setAdapter(new BGABanner.Adapter<ImageView, BannerInfoBean>() {
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
                            .centerCrop()
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

    private void initRefresh() {
        if (isRefresh) {
            swipe_layout.setRefreshing(false);
        } else {
            swipe_layout.setLoadingMore(false);
        }
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
            initRequest();
        } else {
            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
        }
    }


    @Override
    public void onRefresh() {
        isRefresh = true;
        indexfrom = 0;
        if (newsList!=null&&newsList.size()>0){
            newsList.clear();
        }
        initRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        if (newsList!=null&&newsList.size()>0){
            indexfrom = newsList.get(newsList.size()-1).getDataIndex();
        }
        initRequest();
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.NEWS_ZAN)) {
            NewsInfoBean newsInfoBean = eventPostBean.getNewsInfoBean();
            for (int i = 0; i < newsList.size(); i++) {
                if (newsList.get(i).getId().equals(newsInfoBean.getId())
                        &&!newsList.get(i).getIsLike().equals(newsInfoBean.getIsLike())) {
                    newsList.get(i).setLikeCount(newsInfoBean.getLikeCount());
                    newsList.get(i).setIsLike(newsInfoBean.getIsLike());
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.LoginOut)
                ||eventPostBean.getType().equals(Config.PUBLISH_ZAN)){
            userId = SPUtils.getSharedStringData(getActivity(), "userId");
            initRequest();
        }else if (eventPostBean.getType().equals(Config.NEWS_COMMENT_SUCCESS)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < newsList.size(); i++) {
                if (newsList.get(i).getId().equals(articleId)){
                    newsList.get(i).setCommentCount(newsList.get(i).getCommentCount()+1);
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_DEL)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < newsList.size(); i++) {
                if (newsList.get(i).getId().equals(articleId)){
                    newsList.get(i).setCommentCount(newsList.get(i).getCommentCount()-1);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }
}

package com.example.auser.zthacker.ui.fragment.main.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundCommunityAdapter;
import com.example.auser.zthacker.adapter.FoundNewsAdapter;
import com.example.auser.zthacker.adapter.HomeCourseToolRecommendAdapter;
import com.example.auser.zthacker.adapter.HomeTypeChildAdapter;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.BannerInfoBean;
import com.example.auser.zthacker.bean.TeachingResourseInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.bean.HomeData;
import com.example.auser.zthacker.bean.NewsInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.NewsInfoActivity;
import com.example.auser.zthacker.ui.activity.home.ConceptActivity;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;
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
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/4.
 */

public class HomeFragment extends BaseFragment implements OnRefreshListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.banner_home)
    BGABanner banner_home;
    @BindView(R.id.rlv_series)
    RecyclerView rlv_series;
    @BindView(R.id.rlv_class_tool)
    RecyclerView rlv_class_tool;
    @BindView(R.id.tv_know_more)
    TextView tv_know_more;
    @BindView(R.id.tv_know_more2)
    TextView tv_know_more2;
    @BindView(R.id.rv_community)
    RecyclerView rv_community;
    @BindView(R.id.rv_found)
    RecyclerView rv_found;

    private GsonBuilder gsonBuilder;
    private FoundNewsAdapter newsAdapter;
    private HomeData homeData;
    private String code;
    boolean isRefresh = false;
    private List<BannerInfoBean> bannerInfoManage = new ArrayList<>();
    private List<HomeClassTypeInfoBean> courseManage = new ArrayList<>();
    private List<NewsInfoBean> publishInfo = new ArrayList<>();
    private List<TeachingResourseInfoBean> videoList = new ArrayList<>();
    private List<PublishInfoList.PublishInfo> articleList = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    private FoundCommunityAdapter foundCommunityAdapter;
    private String userId;
    private HomeTypeChildAdapter classTypeAdapter;
    private HomeCourseToolRecommendAdapter classToolAdapter;
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private VideoFrameImageLoader mVideoFrameImageLoader1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_home);
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
        setTop(R.color.black);
        userId = SPUtils.getSharedStringData(getActivity(), "userId");
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(getActivity(), RecycleViewDividerLine.VERTICAL_LIST
                , (int) (getActivity().getResources().getDimension(R.dimen.x11))
                , getActivity().getResources().getColor(R.color.white));
        rlv_series.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        rlv_series.setFocusable(false);
        rlv_series.addItemDecoration(recycleViewDividerLine);

        rlv_class_tool.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        rlv_class_tool.setFocusable(false);
        rlv_class_tool.addItemDecoration(recycleViewDividerLine);

        rv_community.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        RecycleViewDividerLine recycleViewDividerLine1 = new RecycleViewDividerLine(getActivity(), RecycleViewDividerLine.HORIZONTAL_LIST,
                R.drawable.view_home_divider);
        rv_community.addItemDecoration(recycleViewDividerLine1);
        rv_community.setFocusable(false);
        rv_community.setNestedScrollingEnabled(false);
        mVideoFrameImageLoader1 = new VideoFrameImageLoader(getActivity(), rv_community);

        rv_found.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rv_found.addItemDecoration(recycleViewDividerLine1);
        rv_found.setFocusable(false);
        rv_found.setNestedScrollingEnabled(false);
        mVideoFrameImageLoader = new VideoFrameImageLoader(getActivity(), rv_found);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setLoadMoreEnabled(false);
    }

    private void initData() {
        OkGo.post(ApiRequestData.getInstance(getActivity()).HomeData)// 请求方式和请求url
                .tag(this)
                .params("sysAppUser", userId)// 请求的 tag, 主要用于取消对应的请求
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        if (!isFirst) {
                            isFirst = true;
                        }
                        NormalObjData<HomeData> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<HomeData>>() {
                                }.getType());
                        homeData = mData.getData();
                        code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(getActivity(), mData.getMsg());
                            return;
                        }
                        if (bannerInfoManage != null) {
                            bannerInfoManage.clear();
                        }
                        bannerInfoManage.addAll(homeData.getBannerInfoManage());

                        if (courseManage != null) {
                            courseManage.clear();
                        }
                        courseManage.addAll(homeData.getCourseManage());

                        if (videoList!=null){
                            videoList.clear();
                        }
                        videoList.addAll(homeData.getVideoList());

                        if (publishInfo != null) {
                            publishInfo.clear();
                        }
                        publishInfo.addAll(homeData.getPublishInfo());
                        if (articleList != null) {
                            articleList.clear();
                        }
                        articleList.addAll(homeData.getArticleList());
                        setBannerData();
                        if (isRefresh) {
                            swipe_layout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(getActivity(), e.getMessage());
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        if (isRefresh) {
                            swipe_layout.setRefreshing(false);
                        }
                    }
                });
    }

    private void setCommunityData() {
        if (videoUrlList != null && videoUrlList.size() > 0) {
            videoUrlList.clear();
        }
        for (int i = 0; i < articleList.size(); i++) {
            if (articleList.get(i).getType().equals("2")) {
                videoUrlList.add(articleList.get(i).getVideoIds());
            } else {
                videoUrlList.add("");
            }
        }
        String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
        mVideoFrameImageLoader.setVideoUrls(videoArray);
        //设置适配器
        if (foundCommunityAdapter == null) {
            foundCommunityAdapter = new FoundCommunityAdapter(getActivity(), articleList, 3);
            foundCommunityAdapter.setVideoUrls(mVideoFrameImageLoader);
            rv_found.setAdapter(foundCommunityAdapter);
            rv_found.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {

                }
                @Override
                public void onChildViewDetachedFromWindow(View view) {
                    if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                        JCVideoPlayer videoPlayer = (JCVideoPlayer) JCVideoPlayerManager.getCurrentJcvd();
                        if (videoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                            JCVideoPlayer.releaseAllVideos();
                        }
                    }
                }
            });
        } else {
            foundCommunityAdapter.setVideoUrls(mVideoFrameImageLoader);
            foundCommunityAdapter.notifyDataSetChanged();
        }
    }

    private void setNewsData() {
        if (videoUrlList != null && videoUrlList.size() > 0) {
            videoUrlList.clear();
        }
        for (int i = 0; i < publishInfo.size(); i++) {
            if (publishInfo.get(i).getShowType().equals("1")) {
                videoUrlList.add(publishInfo.get(i).getAttachList().get(0).getFileUrl());
            } else {
                videoUrlList.add("");
            }
        }
        String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
        mVideoFrameImageLoader1.setVideoUrls(videoArray);
        //设置适配器
        if (newsAdapter == null) {
            newsAdapter = new FoundNewsAdapter(getActivity(), publishInfo, 1);
            newsAdapter.setVideoUrls(mVideoFrameImageLoader1);
            rv_community.setAdapter(newsAdapter);
            newsAdapter.setOnItemClickListener(new FoundNewsAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (publishInfo.get(position).getShowType().equals("1")) {
                        return;
                    }
                    NewsInfoActivity.startActivity(getActivity(), publishInfo.get(position));
                }
            });
            rv_community.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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
            newsAdapter.setVideoUrls(mVideoFrameImageLoader1);
            newsAdapter.notifyDataSetChanged();
        }
    }

    private void setTypeData() {
        if (classTypeAdapter == null) {
            classTypeAdapter = new HomeTypeChildAdapter(getActivity(), courseManage);
            rlv_series.setAdapter(classTypeAdapter);
        } else {
            classTypeAdapter.notifyDataSetChanged();
        }
        setClassToolDate();
    }

    private void setClassToolDate() {
        if (classToolAdapter == null) {
            classToolAdapter = new HomeCourseToolRecommendAdapter(getActivity(), videoList);
            rlv_class_tool.setAdapter(classToolAdapter);
        } else {
            classToolAdapter.notifyDataSetChanged();
        }
        setNewsData();
        setCommunityData();
    }

    private void setBannerData() {
        if (bannerInfoManage != null && bannerInfoManage.size() > 1) {
            banner_home.setAutoPlayAble(true);
        } else {
            banner_home.setAutoPlayAble(false);
        }
        if (bannerInfoManage != null) {
            banner_home.setData(bannerInfoManage, null);
            banner_home.setAdapter(new BGABanner.Adapter<ImageView, BannerInfoBean>() {
                @Override
                public void fillBannerItem(BGABanner banner, ImageView itemView, BannerInfoBean model, int position) {
                    if (!TextUtil.isNull(model.getPicUrl())) {
                        if (!TextUtil.isNull(model.getPicUrl())) {
                            Glide.with(getActivity())
                                    .load(model.getPicUrl())
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .fitCenter()
                                    .placeholder(R.mipmap.img_placeholder_320)
                                    .override(490, 240)
                                    .thumbnail(0.2f)
                                    .into(itemView);
                        }
                    }
                }
            });
            banner_home.setDelegate(new BGABanner.Delegate<ImageView, BannerInfoBean>() {
                @Override
                public void onBannerItemClick(BGABanner banner, ImageView itemView, BannerInfoBean model, int position) {
                    if (!TextUtil.isNull(model.getUrl())) {
                        ConceptActivity.startActivity(getActivity(), model.getUrl(), model.getBannerTitle(), 1);
                    }
                }
            });
        }
        setTypeData();
    }

    @OnClick({R.id.iv_contact_line, R.id.tv_know_more, R.id.tv_class_more,R.id.tv_know_more2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_contact_line:
                ClassConsultActivity.startActivity(getActivity(), Config.CLASS_CONSULT);
                break;
            case R.id.tv_know_more:
                EventBus.getDefault().post(Config.MAIN_FOUND_NEWS);
                break;
            case R.id.tv_class_more:
                EventBus.getDefault().post(Config.MAIN_COURSE);
                break;
            case R.id.tv_know_more2:
                EventBus.getDefault().post(Config.MAIN_FOUND_COMMUNITY);
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

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_TEXT_DEL)) {
            Iterator<PublishInfoList.PublishInfo> iterator = articleList.iterator();
            while (iterator.hasNext()) {
                PublishInfoList.PublishInfo next = iterator.next();
                if (next.getId().equals(eventPostBean.getMessage())) {
                    iterator.remove();
                }
            }
            foundCommunityAdapter.notifyDataSetChanged();
        } else if (eventPostBean.getType().equals(Config.PUBLISH_ZAN)) {
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < articleList.size(); i++) {
                if (articleList.get(i).getId().equals(publishInfo.getId())
                        && articleList.get(i).getIsLike() != publishInfo.getIsLike()) {
                    articleList.get(i).setLikeCount(publishInfo.getLikeCount());
                    articleList.get(i).setIsLike(publishInfo.getIsLike());
                    foundCommunityAdapter.notifyItemChanged(i);
                }
            }
        } else if (eventPostBean.getType().equals(Config.NEWS_ZAN)) {
            NewsInfoBean newsInfoBean = eventPostBean.getNewsInfoBean();
            for (int i = 0; i < publishInfo.size(); i++) {
                if (publishInfo.get(i).getId().equals(newsInfoBean.getId())
                        && !publishInfo.get(i).getIsLike().equals(newsInfoBean.getIsLike())) {
                    publishInfo.get(i).setLikeCount(newsInfoBean.getLikeCount());
                    publishInfo.get(i).setIsLike(newsInfoBean.getIsLike());
                    newsAdapter.notifyItemChanged(i);
                }
            }
        } else if (eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)) {
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < articleList.size(); i++) {
                if (articleList.get(i).getId().equals(publishInfo.getId())
                        && articleList.get(i).getIsCollect() != publishInfo.getIsCollect()) {
                    articleList.get(i).setCollectCount(publishInfo.getCollectCount());
                    articleList.get(i).setIsCollect(publishInfo.getIsCollect());
                    foundCommunityAdapter.notifyItemChanged(i);
                }
            }
        } else if (eventPostBean.getType().equals(Config.PERSONAL_ATTENTION)) {
            String articleUserId = eventPostBean.getId();
            int isFollow = eventPostBean.getCount();
            for (int i = 0; i < articleList.size(); i++) {
                if (articleList.get(i).getSysAppUser().equals(articleUserId)
                        && isFollow != articleList.get(i).getIsFollow()) {
                    articleList.get(i).setIsFollow(isFollow);
                    foundCommunityAdapter.notifyItemChanged(i);
                }
            }
        } else if (eventPostBean.getType().equals(Config.PUBLISH_IMAGE) ||
                eventPostBean.getType().equals(Config.LoginOut)) {
            userId = SPUtils.getSharedStringData(getActivity(), "userId");
            initData();
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_SUCCESS)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < articleList.size(); i++) {
                if (articleList.get(i).getId().equals(articleId)){
                    articleList.get(i).setCommentCount(articleList.get(i).getCommentCount()+1);
                    foundCommunityAdapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.NEWS_COMMENT_SUCCESS)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < publishInfo.size(); i++) {
                if (publishInfo.get(i).getId().equals(articleId)){
                    publishInfo.get(i).setCommentCount(publishInfo.get(i).getCommentCount()+1);
                    newsAdapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_DEL)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < publishInfo.size(); i++) {
                if (publishInfo.get(i).getId().equals(articleId)){
                    publishInfo.get(i).setCommentCount(publishInfo.get(i).getCommentCount()-1);
                    newsAdapter.notifyItemChanged(i);
                }
            }
            for (int i = 0; i < articleList.size(); i++) {
                if (articleList.get(i).getId().equals(articleId)){
                    articleList.get(i).setCommentCount(articleList.get(i).getCommentCount()+1);
                    foundCommunityAdapter.notifyItemChanged(i);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

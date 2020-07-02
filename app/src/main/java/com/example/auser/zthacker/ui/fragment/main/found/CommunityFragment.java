package com.example.auser.zthacker.ui.fragment.main.found;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundCommunityAdapter;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.http.ApiRequestData;
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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017-12-7.
 */

public class CommunityFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipe_layout;
    boolean isRefresh = false;
    private FoundCommunityAdapter adapter;
    public String userId;
    private int indexfrom;
    private GsonBuilder gsonBuilder;
    private List<PublishInfoList.PublishInfo> publishInfoList = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    //private List<BannerInfoBean> bannerInfoManage;
    private String code;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_recycleview);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(getActivity(), RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12), getResources().getColor(R.color.tab_bg_fa));
        recyclerView.addItemDecoration(recycleViewDividerLine);
        recyclerView.setFocusable(false);
        mVideoFrameImageLoader = new VideoFrameImageLoader(getActivity(), recyclerView);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
    }

    private void initAdapter() {
        if (videoUrlList!=null&&videoUrlList.size()>0){
            videoUrlList.clear();
        }
        for (int i = 0;i<publishInfoList.size();i++){
                if (!TextUtil.isNull(publishInfoList.get(i).getType())&&publishInfoList.get(i).getType().equals("2")){
                    videoUrlList.add(publishInfoList.get(i).getVideoIds());
                }else {
                    videoUrlList.add("");
                }
        }
        String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
        mVideoFrameImageLoader.setVideoUrls(videoArray);
        //设置适配器
        if (adapter == null) {
            adapter = new FoundCommunityAdapter(getActivity(), publishInfoList, 0);
            adapter.setVideoUrls(mVideoFrameImageLoader);
            recyclerView.setAdapter(adapter);
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

    public void initRequest() {
        ApiRequestData.getInstance(getActivity()).getPublishList(userId, indexfrom, new StringCallback() {
            @Override
            public void onSuccess(String s,Call call,Response response) {
                initRefresh();
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                if (!isFirst) {
                    isFirst = true;
                }
                NormalObjData<PublishInfoList> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<PublishInfoList>>() {
                        }.getType());
                code = mData.getCode();
                ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                    ToastUtil.show(getActivity(),mData.getMsg());
                    return;
                }
                if (mData.getData().getDataList()==null||mData.getData().getDataList().size()==0){
                    ToastUtil.show(getActivity(),"没有更多了！");
                    return;
                }
                publishInfoList.addAll(mData.getData().getDataList());
                Log.e("indexfrom",indexfrom+"");
                if (publishInfoList != null && publishInfoList.size() > 0) {
                    initAdapter();
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

   /* private void initBannerAdapter() {
        banner_home.setData(bannerInfoManage, null);
        banner_home.setAdapter(new BGABanner.Adapter<ImageView, BannerInfoBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, BannerInfoBean model, int position) {
                if (!TextUtil.isNull(model.getPicUrl())) {
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
    }*/

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
       if (isVisible) {
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
            initRequest();
       } else {
            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
       }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_TEXT_DEL)) {
            Iterator<PublishInfoList.PublishInfo> iterator = publishInfoList.iterator();
            while (iterator.hasNext()) {
                PublishInfoList.PublishInfo next = iterator.next();
                if (next.getId().equals(eventPostBean.getMessage())) {
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
        } else if (eventPostBean.getType().equals(Config.PUBLISH_ZAN)) {
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(publishInfo.getId())
                        &&publishInfoList.get(i).getIsLike()!=publishInfo.getIsLike()) {
                    publishInfoList.get(i).setLikeCount(publishInfo.getLikeCount());
                    publishInfoList.get(i).setIsLike(publishInfo.getIsLike());
                    adapter.notifyItemChanged(i);
                }
            }
        }else if(eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)){
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(publishInfo.getId())
                        &&publishInfoList.get(i).getIsCollect()!=publishInfo.getIsCollect()){
                    publishInfoList.get(i).setCollectCount(publishInfo.getCollectCount());
                    publishInfoList.get(i).setIsCollect(publishInfo.getIsCollect());
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PERSONAL_ATTENTION)){
            String articleUserId = eventPostBean.getId();
            int isFollow = eventPostBean.getCount();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getSysAppUser().equals(articleUserId)
                        &&isFollow!=publishInfoList.get(i).getIsFollow()) {
                    publishInfoList.get(i).setIsFollow(isFollow);
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_IMAGE) ||
                eventPostBean.getType().equals(Config.LoginOut)) {
            userId = SPUtils.getSharedStringData(getActivity(), "userId");
            if (publishInfoList!=null&&publishInfoList.size()>0){
                publishInfoList.clear();
            }
            indexfrom = 0;
            initRequest();
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_SUCCESS)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(articleId)){
                    publishInfoList.get(i).setCommentCount(publishInfoList.get(i).getCommentCount()+1);
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_DEL)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(articleId)){
                    publishInfoList.get(i).setCommentCount(publishInfoList.get(i).getCommentCount()-1);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        if (publishInfoList!=null&&publishInfoList.size()>0){
            publishInfoList.clear();
        }
        indexfrom = 0;
        initRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        if (publishInfoList!=null&&publishInfoList.size()>0){
            indexfrom  = publishInfoList.get(publishInfoList.size()-1).getDataIndex();
        }
        initRequest();
    }

    private void initRefresh() {
        if (isRefresh) {
            swipe_layout.setRefreshing(false);
        } else {
            swipe_layout.setLoadingMore(false);
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

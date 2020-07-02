package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundCommunityAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MyCollectionArticlesBean;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017-11-16.
 */

public class MyCollectionActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    RecyclerView swipe_target;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;

    boolean isRefresh = false;
    private List<PublishInfoList.PublishInfo> publishInfoList;
    private List<String> videoUrlList = new ArrayList<>();
    private FoundCommunityAdapter adapter;
    private String userId;
    private GsonBuilder gsonBuilder;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,MyCollectionActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("我的收藏");
        userId = SPUtils.getSharedStringData(this, "userId");
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
       /* FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);*/
        swipe_target.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        swipe_target.addItemDecoration(recycleViewDividerLine);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, swipe_target);

        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();
    }

    private void initRequest() {
        OkGo.post(ApiRequestData.getInstance(this).MineCollection)
                .tag(this)
                .params("sysAppUser", userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        initRefresh();
                        NormalObjData<MyCollectionArticlesBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<MyCollectionArticlesBean>>() {
                                }.getType());
                        ApiRequestData.getInstance(MyCollectionActivity.this).getDialogDismiss();
                        String code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(MyCollectionActivity.this, mData.getMsg());
                            return;
                        }
                        publishInfoList = mData.getData().getDataList();
                        initAdapter(publishInfoList);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(MyCollectionActivity.this, e.getMessage());
                        initRefresh();
                    }
                });
    }

    public void initAdapter(List<PublishInfoList.PublishInfo> list) {
        if (list==null||list.size()==0){
            iv_empty.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.VISIBLE);
            iv_empty.setImageResource(R.mipmap.img_blankpage_mycollection);
            tv_empty_text.setText("暂无收藏内容");
            tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
        }else {
            iv_empty.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.GONE);
            if (videoUrlList!=null&&videoUrlList.size()>0){
                videoUrlList.clear();
            }
            for (int i = 0;i<publishInfoList.size();i++){
                if (publishInfoList.get(i).getType().equals("2")){
                    videoUrlList.add(publishInfoList.get(i).getVideoIds());
                }else {
                    videoUrlList.add("");
                }
            }
            String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
            mVideoFrameImageLoader.setVideoUrls(videoArray);
            //设置适配器
            if (adapter==null){
                adapter = new FoundCommunityAdapter(this,publishInfoList,2);
                adapter.setVideoUrls(mVideoFrameImageLoader);
                swipe_target.setAdapter(adapter);
                swipe_target.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(View view) {

                    }

                    @Override
                    public void onChildViewDetachedFromWindow(View view) {
                        if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                            Log.e("rrrrrrr","dddffggg");
                            JCVideoPlayer videoPlayer = (JCVideoPlayer) JCVideoPlayerManager.getCurrentJcvd();
                            Log.e("ddddddddd",videoPlayer.currentState+"");
                            if (videoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                                //JCVideoPlayer.releaseAllVideos();
                                videoPlayer.release();
                            }
                        }
                    }
                });
            }else {
                adapter.setVideoUrls(mVideoFrameImageLoader);
                adapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onRefresh() {
        isRefresh = true;
        initRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        initRequest();
    }

    private void initRefresh() {
        if (isRefresh) {
            swipe_layout.setRefreshing(false);
        } else {
            swipe_layout.setLoadingMore(false);
        }
    }

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_IMAGE)){
            userId = SPUtils.getSharedStringData(this, "userId");
            initRequest();
        }else if (eventPostBean.getType().equals(Config.PUBLISH_ZAN)) {
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(publishInfo.getId())
                        &&publishInfoList.get(i).getIsLike()!=publishInfo.getIsLike()) {
                    //publishInfoList.set(i, publishInfo);
                    publishInfoList.get(i).setLikeCount(publishInfo.getLikeCount());
                    publishInfoList.get(i).setIsLike(publishInfo.getIsLike());
                }
            }
        }else if(eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)){
            Iterator<PublishInfoList.PublishInfo> iterator = publishInfoList.iterator();
            while (iterator.hasNext()) {
                PublishInfoList.PublishInfo next = iterator.next();
                if (next.getId().equals(eventPostBean.getPublishInfo().getId())) {
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
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

        }else if (eventPostBean.getType().equals(Config.PUBLISH_TEXT_DEL)) {
            Iterator<PublishInfoList.PublishInfo> iterator = publishInfoList.iterator();
            while (iterator.hasNext()) {
                PublishInfoList.PublishInfo next = iterator.next();
                if (next.getId().equals(eventPostBean.getMessage())) {
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
        EventBus.getDefault().unregister(this);
    }
}

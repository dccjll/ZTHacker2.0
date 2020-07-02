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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundCommunityAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MyCollectionArticlesBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.bean.UserHomePageInfo;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import com.example.auser.zthacker.view.GlideCircleTransform;
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
 * Created by zhengkq on 2017-11-17.
 */

public class UserHomePageActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener, View.OnClickListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.simpleDraweeView_head_icon)
    ImageView simpleDraweeView_head_icon;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.tv_is_attention)
    TextView tv_is_attention;
    @BindView(R.id.tv_publish_count)
    TextView tv_publish_count;
    @BindView(R.id.tv_attention_count)
    TextView tv_attention_count;
    @BindView(R.id.tv_fans_count)
    TextView tv_fans_count;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;

    private List<PublishInfoList.PublishInfo> list = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    boolean isRefresh = false;
    private GsonBuilder gsonBuilder;
    private UserHomePageInfo userHomePageInfo;
    private FoundCommunityAdapter adapter;
    private String id;
    private String userId;
    private EventPostBean eventPostBean;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, UserHomePageActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setTop(R.color.transparent_half);
        userId = SPUtils.getSharedStringData(this, "userId");
        id = getIntent().getStringExtra("id");
        Log.e("dddddddddd","userId="+userId+"/id="+id);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        recyclerView.addItemDecoration(recycleViewDividerLine);
        recyclerView.setNestedScrollingEnabled(false);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, recyclerView);
        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initRequest() {
        OkGo.post(ApiRequestData.getInstance(this).PublishUserHomePage)
                .tag(this)
                .params("sysAppUser", id)
                .params("loginSysAppUser",userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        NormalObjData<UserHomePageInfo> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<UserHomePageInfo>>() {
                                }.getType());
                        initRefresh();
                        ApiRequestData.getInstance(UserHomePageActivity.this).getDialogDismiss();
                        String code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(UserHomePageActivity.this, mData.getMsg());
                            return;
                        }
                        userHomePageInfo = mData.getData();
                        if (userHomePageInfo != null) {
                            setUserInfo();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(UserHomePageActivity.this, e.getMessage());
                        initRefresh();
                    }
                });

        ApiRequestData.getInstance(this).getUserPublishList(id,new StringCallback(){
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
                ApiRequestData.getInstance(UserHomePageActivity.this).getDialogDismiss();
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")) {
                    ToastUtil.show(UserHomePageActivity.this, mData.getMsg());
                    return;
                }
                if (mData.getData().getDataList()==null){
                    return;
                }else {
                    list.clear();
                }
                list.addAll(mData.getData().getDataList());
                initAdapter();
                Log.e("9999999","request"+System.currentTimeMillis());
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtil.show(UserHomePageActivity.this, e.getMessage());
                ApiRequestData.getInstance(UserHomePageActivity.this).getDialogDismiss();
                initRefresh();
            }
        });
    }

    private void setUserInfo() {
        Glide.with(this)
                .load(userHomePageInfo.getSysAppUerImg())
                .transform(new GlideCircleTransform(this))
                .placeholder(R.mipmap.icon_userphote_hl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .dontAnimate()
                .override(96,96)
                .thumbnail(0.2f)
                .into(simpleDraweeView_head_icon);
        tv_username.setText(userHomePageInfo.getSysAppUserName());
        tv_is_attention.setText(TextUtil.isNull(userHomePageInfo.getIsFollow())
                &&userHomePageInfo.getIsFollow().equals("1")?"已关注":"+关注");
        tv_is_attention.setTextColor(TextUtil.isNull(userHomePageInfo.getIsFollow())
                &&userHomePageInfo.getIsFollow().equals("1")?getResources().getColor(R.color.white):getResources().getColor(R.color.text_32));
        tv_is_attention.setBackground(TextUtil.isNull(userHomePageInfo.getIsFollow())
                &&userHomePageInfo.getIsFollow().equals("1")?getResources().getDrawable(R.drawable.attention_bg):getResources().getDrawable(R.drawable.attention_s_bg));
        tv_publish_count.setText(userHomePageInfo.getArticlesCount() + " 发布");
        tv_attention_count.setText(userHomePageInfo.getFollowCount()+" 关注");
        tv_fans_count.setText(userHomePageInfo.getFollowMeCount()+" 粉丝");

        if (!TextUtil.isNull(id)&&id.equals(userId)){
            tv_is_attention.setVisibility(View.GONE);
        }else {
            tv_is_attention.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        if (videoUrlList!=null&&videoUrlList.size()>0){
            videoUrlList.clear();
        }
        for (int i = 0;i<list.size();i++){
            if (list.get(i).getType().equals("2")){
                videoUrlList.add(list.get(i).getVideoIds());
            }else {
                videoUrlList.add("");
            }
        }
        String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
        mVideoFrameImageLoader.setVideoUrls(videoArray);
        if (adapter == null) {
            adapter = new FoundCommunityAdapter(this, list, 1);
            adapter.setVideoUrls(mVideoFrameImageLoader);
            recyclerView.setAdapter(adapter);
            recyclerView.setFocusable(false);
            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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
        } else {
            adapter.setVideoUrls(mVideoFrameImageLoader);
            adapter.notifyDataSetChanged();
        }
        if (list == null || list.size() == 0) {
            iv_empty.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.VISIBLE);
            iv_empty.setImageResource(R.mipmap.img_blankpage_mycollection);
            tv_empty_text.setText("暂无发布内容");
            tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
        } else {
            //设置适配器
            iv_empty.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.GONE);
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

    @OnClick({R.id.ll_back,R.id.tv_is_attention})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_is_attention:
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(UserHomePageActivity.this);
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.get(ApiRequestData.getInstance(this).PublishTextAttention)
                        .tag(this)
                        .params("sysAppUser", userId)
                        .params("sysAppUser2", id)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(UserHomePageActivity.this).getDialogDismiss();
                                NormalObjData<UtilBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                        }.getType());
                                String code = mData.getCode();
                                ToastUtil.show(UserHomePageActivity.this, mData.getMsg());
                                if (!TextUtil.isNull(code) && code.equals("0")) {
                                    return;
                                }
                                if (mData.getData() != null) {
                                    if (userHomePageInfo!=null){
                                        userHomePageInfo.setIsFollow(mData.getData().getState()+"");
                                    }
                                    setUserInfo();
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.PERSONAL_ATTENTION);
                                    eventPostBean.setMessage(userHomePageInfo.getIsFollow());
                                    eventPostBean.setId(id);
                                    EventBus.getDefault().post(eventPostBean);
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(UserHomePageActivity.this, e.getMessage());
                            }
                        });
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_TEXT_DEL)) {
            Iterator<PublishInfoList.PublishInfo> iterator = list.iterator();
            while (iterator.hasNext()) {
                PublishInfoList.PublishInfo next = iterator.next();
                if (next.getId().equals(eventPostBean.getMessage())) {
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
        } else if (eventPostBean.getType().equals(Config.PUBLISH_ZAN)) {
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(publishInfo.getId())
                        &&list.get(i).getIsLike()!=publishInfo.getIsLike()) {
                    //publishInfoList.set(i, publishInfo);
                    list.get(i).setLikeCount(publishInfo.getLikeCount());
                    list.get(i).setIsLike(publishInfo.getIsLike());
                }
                adapter.notifyDataSetChanged();
            }
        }else if(eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)){
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(publishInfo.getId())
                        &&list.get(i).getIsCollect()!=publishInfo.getIsCollect()) {
                    list.get(i).setCollectCount(publishInfo.getCollectCount());
                    list.get(i).setIsCollect(publishInfo.getIsCollect());
                }
            }
        }else if (eventPostBean.getType().equals(Config.PERSONAL_ATTENTION)){
            String articleUserId = eventPostBean.getId();
            int isFollow = eventPostBean.getCount();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getSysAppUser().equals(articleUserId)
                        &&isFollow!=list.get(i).getIsFollow()) {
                    list.get(i).setIsFollow(isFollow);
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.LoginOut)) {
            userId = SPUtils.getSharedStringData(this, "userId");
            initRequest();
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_SUCCESS)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(articleId)){
                    list.get(i).setCommentCount(list.get(i).getCommentCount()+1);
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_DEL)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(articleId)){
                    list.get(i).setCommentCount(list.get(i).getCommentCount()-1);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        Log.e("9999999","activitypause"+System.currentTimeMillis());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        EventBus.getDefault().unregister(this);
        OkGo.getInstance().cancelTag(this);
        Log.e("9999999","activitydestroy"+System.currentTimeMillis());
    }
}

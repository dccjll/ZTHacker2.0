package com.example.auser.zthacker.ui.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyMsgCommentAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MsgAdapterListBean;
import com.example.auser.zthacker.bean.MsgZanListBean;
import com.example.auser.zthacker.bean.MyCollectionArticlesBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.CommunityPicInfoActivity;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017-11-16.
 */

public class MyZanMesActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    RecyclerView swipe_target;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;

    boolean isRefresh = false;
    private MyMsgCommentAdapter adapter;
    private String userId;
    private GsonBuilder gsonBuilder;
    private List<MsgZanListBean> zanList;
    private List<PublishInfoList.PublishInfo> publishInfoList;
    private List<MsgZanListBean> haveZanList = new ArrayList<>();
    private List<MsgAdapterListBean> haveZanPublishInfoList = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private int zanMsgIndex;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyZanMesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("我的点赞");
        userId = SPUtils.getSharedStringData(this, "userId");
        SPUtils.setSharedIntData(this,"likeCount",0);
        EventPostBean eventPostBean = new EventPostBean();
        eventPostBean.setType(Config.MSG_NOTICE);
        EventBus.getDefault().post(eventPostBean);

        swipe_target.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        swipe_target.addItemDecoration(recycleViewDividerLine);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, swipe_target);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);

        ApiRequestData.getInstance(this).ShowDialog(null);
        initZanRequest();
    }

    private void initZanRequest() {
        if (haveZanList != null) {
            haveZanList.clear();
        }
        ApiRequestData.getInstance(this).getMsgMyPublishZanList(userId, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                ApiRequestData.getInstance(MyZanMesActivity.this).getDialogDismiss();
                initRefresh();
                NormalObjData<List<MsgZanListBean>> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<List<MsgZanListBean>>>() {
                        }.getType());
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")) {
                    ToastUtil.show(MyZanMesActivity.this, mData.getMsg());
                    return;
                }
                zanList = mData.getData();
                zanMsgIndex = mData.getDataIndex();
                SPUtils.setSharedIntData(MyZanMesActivity.this,"zanMsgIndex",zanMsgIndex);
                if (zanList == null || zanList.size() == 0) {
                    initAdapter();
                } else {
                    boolean isHavenZan = false;
                    for (int i = 0; i < zanList.size(); i++) {
                        if (zanList.get(i).getLikeList() != null && zanList.get(i).getLikeList().size() > 0) {
                            isHavenZan = true;
                            haveZanList.add(zanList.get(i));
                        }
                    }
                    if (isHavenZan) {
                        initPublishRequest();
                    }else {
                        initAdapter();
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                initRefresh();
                ToastUtil.show(MyZanMesActivity.this, e.getMessage());
            }
        });
    }

    private void initPublishRequest() {
        if (haveZanPublishInfoList!=null){
            haveZanPublishInfoList.clear();
        }
        ApiRequestData.getInstance(this).getUserPublishList(userId, new StringCallback() {
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
                ApiRequestData.getInstance(MyZanMesActivity.this).getDialogDismiss();
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")) {
                    ToastUtil.show(MyZanMesActivity.this, mData.getMsg());
                    return;
                }
                publishInfoList = mData.getData().getDataList();

                for (int j = 0;j<publishInfoList.size();j++){
                    String articleId = publishInfoList.get(j).getId();
                    for (int i = 0;i<haveZanList.size(); i++){
                        if (articleId.equals(haveZanList.get(i).getArticleId())){
                            MsgAdapterListBean msgAdapterListBean = new MsgAdapterListBean();
                            msgAdapterListBean.setArticles(publishInfoList.get(j));
                            msgAdapterListBean.setZanBean(haveZanList.get(i));
                            haveZanPublishInfoList.add(msgAdapterListBean);
                        }
                    }
                }
                initAdapter();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtil.show(MyZanMesActivity.this, e.getMessage());
                initRefresh();
            }
        });
    }

    private void initAdapter() {
        //设置适配器
        if (zanList==null||zanList.size()==0||haveZanList==null||haveZanList.size()==0){
            iv_empty.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.VISIBLE);
            swipe_layout.setVisibility(View.GONE);
            iv_empty.setImageResource(R.mipmap.img_blankpage_zan);
            tv_empty_text.setText("暂无点赞消息");
            tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
        }else {
            iv_empty.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.GONE);
            swipe_layout.setVisibility(View.VISIBLE);
            if (videoUrlList != null && videoUrlList.size() > 0) {
                videoUrlList.clear();
            }
            for (int i = 0; i < haveZanPublishInfoList.size(); i++) {
                if (haveZanPublishInfoList.get(i).getArticles().getType().equals("2")) {
                    videoUrlList.add(haveZanPublishInfoList.get(i).getArticles().getVideoIds());
                } else {
                    videoUrlList.add("");
                }
            }
            String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
            mVideoFrameImageLoader.setVideoUrls(videoArray);
            if (adapter == null) {
                adapter = new MyMsgCommentAdapter(this,haveZanPublishInfoList,0);
                adapter.setVideoUrls(mVideoFrameImageLoader);
                swipe_target.setAdapter(adapter);
                adapter.setOnItemClickListener(new MyMsgCommentAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CommunityPicInfoActivity.startActivity(MyZanMesActivity.this, haveZanPublishInfoList.get(position).getArticles(),position);
                    }
                });
            } else {
                adapter.setVideoUrls(mVideoFrameImageLoader);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        if (zanList!=null&&zanList.size()>0){
            zanList.clear();
        }
        if (publishInfoList!=null&&publishInfoList.size()>0){
            publishInfoList.clear();
        }
        if (haveZanList!=null&&haveZanList.size()>0){
            haveZanList.clear();
        }
        if (haveZanPublishInfoList!=null&&haveZanPublishInfoList.size()>0){
            haveZanPublishInfoList.clear();
        }
        initZanRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        if (zanList!=null&&zanList.size()>0){
            zanList.clear();
        }
        if (publishInfoList!=null&&publishInfoList.size()>0){
            publishInfoList.clear();
        }
        if (haveZanList!=null&&haveZanList.size()>0){
            haveZanList.clear();
        }
        if (haveZanPublishInfoList!=null&&haveZanPublishInfoList.size()>0){
            haveZanPublishInfoList.clear();
        }
        initZanRequest();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
        EventBus.getDefault().unregister(this);
    }
}

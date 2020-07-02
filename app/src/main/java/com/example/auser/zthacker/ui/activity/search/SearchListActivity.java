package com.example.auser.zthacker.ui.activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundCommunityAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.bean.SearchListBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.LocalSearchUtils;
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
 * Created by zhengkq on 2017/10/30.
 */

public class SearchListActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.iv_search_close)
    ImageView iv_search_close;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;
    private List<PublishInfoList.PublishInfo> publishInfoList = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    private FoundCommunityAdapter adapter;
    boolean isRefresh = false;
    private String searchText;
    private GsonBuilder gsonBuilder;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    public static void startActivity(Context context,String searchText){
        Intent intent = new Intent(context,SearchListActivity.class);
        intent.putExtra("searchText",searchText);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        Intent intent = getIntent();
        searchText = intent.getStringExtra("searchText");
        et_input.setText(searchText);
        et_input.setSelection(searchText.length());

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        recyclerView.addItemDecoration(recycleViewDividerLine);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, recyclerView);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);

        initRequest();

        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            SearchListActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(SearchListActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    searchText = et_input.getText().toString().trim();
                    if (searchText.length()==0){
                        Toast.makeText(SearchListActivity.this, "请输入要搜索的文字！", Toast.LENGTH_SHORT).show();
                    }else {
                        LocalSearchUtils.saveSearchHistory(SearchListActivity.this,searchText);
                        initRequest();
                    }
                }
                return false;
            }
        });
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = et_input.getText().toString().trim();
                if(searchText.length()>0){
                    iv_search_close.setVisibility(View.VISIBLE);
                }else {
                    iv_search_close.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initRequest() {
        ApiRequestData.getInstance(this).getSearchList(searchText,new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                initRefresh();
                NormalObjData<SearchListBean> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<SearchListBean>>() {
                        }.getType());
                ApiRequestData.getInstance(SearchListActivity.this).getDialogDismiss();
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                    ToastUtil.show(SearchListActivity.this, mData.getMsg());
                    return;
                }
                if (mData.getData().getArticleList()==null||mData.getData().getArticleList().size()==0){
                    setEmptyView();
                    return;
                }
                if (publishInfoList!=null&&publishInfoList.size()>0){
                    publishInfoList.clear();
                }
                publishInfoList.addAll(mData.getData().getArticleList());
                initAdapter(publishInfoList);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtil.show(SearchListActivity.this, e.getMessage());
                initRefresh();
            }
        });
    }

    private void setEmptyView() {
        recyclerView.setVisibility(View.GONE);
        iv_empty.setVisibility(View.VISIBLE);
        tv_empty_text.setVisibility(View.VISIBLE);
        iv_empty.setImageResource(R.mipmap.img_blankpage_search);
        tv_empty_text.setText("没有找到相关内容，换个词试试吧");
        tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
    }

    public void initAdapter(List<PublishInfoList.PublishInfo> list) {
            iv_empty.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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
                //设置适配器
                adapter = new FoundCommunityAdapter(this,publishInfoList,2);
                adapter.setVideoUrls(mVideoFrameImageLoader);
                recyclerView.setAdapter(adapter);
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
            }else {
                adapter.setVideoUrls(mVideoFrameImageLoader);
                adapter.notifyDataSetChanged();
            }
    }

    @OnClick({R.id.tv_cancel,R.id.iv_search_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.iv_search_close:
                et_input.setText("");
                break;
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        if (publishInfoList!=null&&publishInfoList.size()>0){
            publishInfoList.clear();
        }
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
                    //publishInfoList.set(i, publishInfo);
                    publishInfoList.get(i).setLikeCount(publishInfo.getLikeCount());
                    publishInfoList.get(i).setIsLike(publishInfo.getIsLike());
                }
            }
        }else if(eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)){
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(publishInfo.getId())
                        &&publishInfoList.get(i).getIsCollect()!=publishInfo.getIsCollect()) {
                    publishInfoList.get(i).setCollectCount(publishInfo.getCollectCount());
                    publishInfoList.get(i).setIsCollect(publishInfo.getIsCollect());
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
    }
}

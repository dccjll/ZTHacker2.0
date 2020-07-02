package com.example.auser.zthacker.ui.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyMsgCommentAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.CommunityCommentBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MsgAdapterListBean;
import com.example.auser.zthacker.bean.MsgCommentListBean;
import com.example.auser.zthacker.bean.MyCollectionArticlesBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.CommunityPicInfoActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.SoftKeyBoardListener;
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

public class MyCommentMesActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    RecyclerView swipe_target;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;
    @BindView(R.id.rl_input_comment_text)
    RelativeLayout rl_input_comment_text;
    @BindView(R.id.input_comment_text)
    EditText input_comment_text;


    boolean isRefresh = false;
    private MyMsgCommentAdapter adapter;
    private String userId;
    private GsonBuilder gsonBuilder;
    private List<CommunityCommentBean> commentList;
    private List<MsgAdapterListBean> haveCommentPublishInfoList = new ArrayList<>();
    private String inputtext;
    private List<String> videoUrlList = new ArrayList<>();
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private List<PublishInfoList.PublishInfo> articleList;
    private int commentMsgIndex;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,MyCommentMesActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_msg);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setCentreText("我的评论");
        userId = SPUtils.getSharedStringData(this, "userId");
        SPUtils.setSharedIntData(this,"commentCount",0);
        EventPostBean eventPostBean = new EventPostBean();
        eventPostBean.setType(Config.MSG_NOTICE);
        EventBus.getDefault().post(eventPostBean);

        SoftKeyBoardListener.setListener(MyCommentMesActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                input_comment_text.setFocusable(false);
                rl_input_comment_text.setVisibility(View.INVISIBLE);
            }
        });

        swipe_target.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        swipe_target.addItemDecoration(recycleViewDividerLine);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, swipe_target);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
        ApiRequestData.getInstance(this).ShowDialog(null);
        initCommentRequest();
    }

    private void initCommentRequest() {
        ApiRequestData.getInstance(this).getMsgPublishCommentList(userId, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                initRefresh();
                ApiRequestData.getInstance(MyCommentMesActivity.this).getDialogDismiss();
                NormalObjData<MsgCommentListBean> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<MsgCommentListBean>>() {
                        }.getType());
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                    ToastUtil.show(MyCommentMesActivity.this, mData.getMsg());
                    return;
                }
                MsgCommentListBean msgCommentListBean = mData.getData();
                commentList = msgCommentListBean.getCommentList();
                articleList = msgCommentListBean.getArticleList();
                if (commentList!=null&&commentList.size()>0){
                    commentMsgIndex = commentList.get(0).getDataIndex();
                }
                SPUtils.setSharedIntData(MyCommentMesActivity.this,"commentMsgIndex", commentMsgIndex);
                Log.e("commentList",commentList.size()+"");
                if (articleList !=null&& articleList.size()>0){
                    for (int i = 0; i< articleList.size(); i++){
                        String id = articleList.get(i).getId();
                        boolean firstHasComment = true;
                        for (int j = 0;j<commentList.size();j++){
                            if (j==0){
                                SPUtils.setSharedIntData(MyCommentMesActivity.this,"commentMsgIndex",commentList.get(j).getDataIndex());
                            }
                            if (commentList.get(j).getCommunityId().equals(id)){
                                if (firstHasComment){
                                    MsgAdapterListBean msgAdapterListBean = new MsgAdapterListBean();
                                    List<CommunityCommentBean> articleCommentList = new ArrayList<>();
                                    msgAdapterListBean.setArticles(articleList.get(i));
                                    msgAdapterListBean.setCommetList(articleCommentList);
                                    haveCommentPublishInfoList.add(msgAdapterListBean);
                                    firstHasComment = false;
                                }
                                haveCommentPublishInfoList.get(i).getCommetList().add(commentList.get(j));
                            }
                        }
                    }
                }
                initAdapter();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ApiRequestData.getInstance(MyCommentMesActivity.this).getDialogDismiss();
                initRefresh();
                ToastUtil.show(MyCommentMesActivity.this, e.getMessage());
            }
        });
    }

    private void initAdapter() {
        //设置适配器
        if (commentList==null||commentList.size()==0||articleList==null||articleList.size()==0){
            iv_empty.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.VISIBLE);
            swipe_layout.setVisibility(View.GONE);
            iv_empty.setImageResource(R.mipmap.img_blankpage_comments);
            tv_empty_text.setText("暂无评论消息");
            tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
        }else {
            iv_empty.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.GONE);
            swipe_layout.setVisibility(View.VISIBLE);
            if (videoUrlList != null && videoUrlList.size() > 0) {
                videoUrlList.clear();
            }
            for (int i = 0; i < haveCommentPublishInfoList.size(); i++) {
                if (haveCommentPublishInfoList.get(i).getArticles().getType().equals("2")) {
                    videoUrlList.add(haveCommentPublishInfoList.get(i).getArticles().getVideoIds());
                } else {
                    videoUrlList.add("");
                }
            }
            String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
            mVideoFrameImageLoader.setVideoUrls(videoArray);
            if (adapter == null) {
                adapter = new MyMsgCommentAdapter(this,haveCommentPublishInfoList,1);
                adapter.setVideoUrls(mVideoFrameImageLoader);
                swipe_target.setAdapter(adapter);
                adapter.setOnItemClickListener(new MyMsgCommentAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CommunityPicInfoActivity.startActivity(MyCommentMesActivity.this, haveCommentPublishInfoList.get(position).getArticles(),position);
                    }
                });
            } else {
                adapter.setVideoUrls(mVideoFrameImageLoader);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void showSoftKeyboard(final PublishInfoList.PublishInfo publishInfo, final CommunityCommentBean communityCommentBean1) {
        input_comment_text.setFocusable(true);
        input_comment_text.setFocusableInTouchMode(true);
        input_comment_text.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(input_comment_text, 0);
        rl_input_comment_text.setVisibility(View.VISIBLE);
        input_comment_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    inputtext = input_comment_text.getText().toString();
                    if (inputtext.length() == 0) {
                        Toast.makeText(MyCommentMesActivity.this, "请输入要发表的文字！", Toast.LENGTH_SHORT).show();
                    } else {
                        ApiRequestData.getInstance(MyCommentMesActivity.this).ShowDialog(null);
                        OkGo.post(ApiRequestData.getInstance(MyCommentMesActivity.this).PublishReplyComment)
                                .tag(this)
                                .params("communityArticlesId", publishInfo.getId())
                                .params("parentId", communityCommentBean1 == null ? "" : communityCommentBean1.getId())
                                .params("sysAppUser", userId)
                                .params("sysAppUser2", communityCommentBean1 == null ? "" : communityCommentBean1.getSysAppUser())
                                .params("content", inputtext)
                                .params("relevantId", communityCommentBean1 == null ? "" : communityCommentBean1.getId())
                                .params(" commentType","1")
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(MyCommentMesActivity.this).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        ToastUtil.show(MyCommentMesActivity.this, mData.getMsg());
                                        if (!TextUtil.isNull(code) && code.equals("0")) {
                                            return;
                                        }
                                        input_comment_text.getText().clear();

                                        InputMethodManager inputMethodManager = (InputMethodManager)
                                                MyCommentMesActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                                                InputMethodManager.HIDE_NOT_ALWAYS);

                                        //评论成功，刷新界面
                                        initCommentRequest();
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(MyCommentMesActivity.this, e.getMessage());
                                    }
                                });
                    }
                }
                return false;
            }
        });
    }
    @Override
    public void onRefresh() {
        isRefresh = true;
        if (commentList!=null&&commentList.size()>0){
            commentList.clear();
        }
        if (articleList!=null&&articleList.size()>0){
            articleList.clear();
        }
        if (haveCommentPublishInfoList!=null&&haveCommentPublishInfoList.size()>0){
            haveCommentPublishInfoList.clear();
        }
        initCommentRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        if (commentList!=null&&commentList.size()>0){
            commentList.clear();
        }
        if (articleList!=null&&articleList.size()>0){
            articleList.clear();
        }
        if (haveCommentPublishInfoList!=null&&haveCommentPublishInfoList.size()>0){
            haveCommentPublishInfoList.clear();
        }
        initCommentRequest();
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
    }
}

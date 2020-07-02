package com.example.auser.zthacker.ui.activity.found;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FirstClassCommentAdapter;
import com.example.auser.zthacker.app.AppApplication;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.CommunityCommentBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NewsInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.ShareDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.SoftKeyBoardListener;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/9/2.
 */

public class NewsInfoActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.tv_comment_count)
    TextView tv_comment_count;
    @BindView(R.id.view_empty)
    View view_empty;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;
    @BindView(R.id.webapp)
    WebView webapp;
    @BindView(R.id.input_comment)
    EditText input_comment;
    @BindView(R.id.rl_input_comment_text)
    RelativeLayout rl_input_comment_text;
    @BindView(R.id.input_comment_text)
    EditText input_comment_text;
    @BindView(R.id.iv_zan)
    ImageView iv_zan;
    @BindView(R.id.tv_zan_count)
    TextView tv_zan_count;
    @BindView(R.id.iv_collection)
    ImageView iv_collection;
    @BindView(R.id.tv_collection_count)
    TextView tv_collection_count;
    @BindView(R.id.ll_collection)
    LinearLayout ll_collection;
    private String url;
    private String inputtext;
    private String userId;
    private GsonBuilder gsonBuilder;
    private String code;
    boolean isRefresh = false;
    private List<CommunityCommentBean> list = new ArrayList<>();
    private FirstClassCommentAdapter adapter;
    private NewsInfoBean newsInfoBean;
    /*private SharePlatform oks;*/
    private ShareDialog shareDialog;
    private EventPostBean eventPostBean;
    private int indexfrom = -1;
    private boolean loadMore;

    public static void startActivity(Context context, NewsInfoBean newsInfoBean) {
        Intent intent = new Intent(context, NewsInfoActivity.class);
        intent.putExtra("newsInfoBean", (Serializable) newsInfoBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        newsInfoBean = (NewsInfoBean) intent.getSerializableExtra("newsInfoBean");
        AppApplication.getInstance().setCurrentArticleId(newsInfoBean.getId());
        userId = SPUtils.getSharedStringData(this, "userId");
        if (TextUtil.isNull(newsInfoBean.getArticleUrl())) {
            url = newsInfoBean.getContentUrl() + "?id=" + newsInfoBean.getId();
        } else {
            url = newsInfoBean.getArticleUrl();
        }
        setCentreText(newsInfoBean.getArticleTitle());
        Log.e("url", url);
        //设置WebView属性，能够执行Javascript脚本
        webapp.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error);
            }
        });
        //设置WebView属性，能够执行Javascript脚本
        webapp.getSettings().setJavaScriptEnabled(true);
        webapp.getSettings().setDomStorageEnabled(true);
        //加载需要显示的网页webapp
        webapp.loadUrl(url);
        webapp.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e("dsfgggggggg",error.getPrimaryError()+"");
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("dfdfdffdfd","开始加载网页...");
                super.onPageStarted(view, url, favicon);
                //ApiRequestData.getInstance(NewsInfoActivity.this).ShowDialog("正在加载中...");
            }

            @Override

            public void onLoadResource(WebView view, String url) {

                Log.e("dfdfdffdfd","开始加载资源...");
                super.onLoadResource(view, url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //页面加载完毕
                Log.e("dfdfdffdfd","网页加载成功...");
                super.onPageFinished(view,url);
                //ApiRequestData.getInstance(NewsInfoActivity.this).getDialogDismiss();
            }

        });
        iv_zan.setImageResource(newsInfoBean.getIsLike().equals("1") ? R.mipmap.bottombar_icon_zan_hl : R.mipmap.bottombar_icon_zan);
        tv_zan_count.setText(newsInfoBean.getLikeCount() + "");
        SoftKeyBoardListener.setListener(NewsInfoActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                input_comment.setText(input_comment_text.getText().toString());
                input_comment_text.setFocusable(false);
                rl_input_comment_text.setVisibility(View.INVISIBLE);
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                2, getResources().getColor(R.color.part_divider_2)));
        recyclerView.setNestedScrollingEnabled(false);

        ll_collection.setVisibility(View.GONE);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();
    }

    @OnClick({R.id.image_back, R.id.input_comment, R.id.ll_zan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.input_comment:
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(this);
                    return;
                }
                showSoftKeyboard(null);
                break;
            case R.id.ll_zan:
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(this);
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.get(ApiRequestData.getInstance(this).PublishTextLike)//点赞
                        .tag(this)
                        .params("objectId", newsInfoBean.getId())
                        .params("sysAppUser", userId)
                        .params("likeType", "2")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(NewsInfoActivity.this).getDialogDismiss();
                                NormalObjData<UtilBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                        }.getType());
                                String code = mData.getCode();
                                //ToastUtil.show(context, mData.getMsg());
                                if (!TextUtil.isNull(code) && code.equals("0")) {
                                    return;
                                }
                                if (mData.getData() != null) {
                                    if (mData.getData().getState() == 0) {
                                        newsInfoBean.setLikeCount(newsInfoBean.getLikeCount() - 1);
                                    } else {
                                        newsInfoBean.setLikeCount(newsInfoBean.getLikeCount() + 1);
                                    }
                                    newsInfoBean.setIsLike(mData.getData().getState()+"");
                                    tv_zan_count.setText(newsInfoBean.getLikeCount() + "");
                                    iv_zan.setImageResource(mData.getData().getState() == 0
                                            ? R.mipmap.bottombar_icon_zan : R.mipmap.bottombar_icon_zan_hl);
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.NEWS_ZAN);
                                    eventPostBean.setNewsInfoBean(newsInfoBean);
                                    EventBus.getDefault().post(eventPostBean);
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(NewsInfoActivity.this, e.getMessage());
                            }
                        });
                break;
            /*case R.id.ll_share:

                oks = new SharePlatform(this);
                if (shareDialog==null){
                    shareDialog = new ShareDialog(this);
                    WindowManager.LayoutParams attributes = shareDialog.getWindow().getAttributes();
                    attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
                    attributes.gravity = Gravity.BOTTOM;
                    shareDialog.setCanceledOnTouchOutside(true);
                }
                shareDialog.show();
                shareDialog.setOnClickChoose(new ShareDialog.OneShareListener() {
                    @Override
                    public void onWXPYQ() {
                        oks.share("WechatMoments",newsInfoBean.getArticleTitle()
                                ,"",null,url);
                    }

                    @Override
                    public void onWXHY() {
                        oks.share("Wechat",newsInfoBean.getArticleTitle()
                                ,"",null,url);
                    }

                    @Override
                    public void onQQ() {
                        oks.share("QQ",newsInfoBean.getArticleTitle()
                                ,"",null,url);
                    }

                    @Override
                    public void onSina() {
                        oks.share("SinaWeibo",newsInfoBean.getArticleTitle()
                                ,"",null,url);
                    }
                });
                break;*/
        }
    }

    public void showSoftKeyboard(final CommunityCommentBean communityCommentBean) {
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
                    input_comment.setText(inputtext);
                    if (inputtext.length() == 0) {
                        Toast.makeText(NewsInfoActivity.this, "请输入要发表的文字！", Toast.LENGTH_SHORT).show();
                    } else {
                        ApiRequestData.getInstance(NewsInfoActivity.this).ShowDialog(null);
                        OkGo.post(ApiRequestData.getInstance(NewsInfoActivity.this).PublishReplyComment)
                                .tag(this)
                                .params("communityArticlesId", newsInfoBean.getId())
                                .params("parentId", communityCommentBean == null ? "" : communityCommentBean.getId())
                                .params("sysAppUser", userId)
                                .params("sysAppUser2", communityCommentBean == null ? "" : communityCommentBean.getSysAppUser())
                                .params("content", inputtext)
                                .params("relevantId", communityCommentBean == null ? "" : communityCommentBean.getId())
                                .params(" commentType", "2")
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(NewsInfoActivity.this).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        ToastUtil.show(NewsInfoActivity.this, mData.getMsg());
                                        if (!TextUtil.isNull(code) && code.equals("0")) {
                                            return;
                                        }
                                        input_comment.getText().clear();
                                        input_comment_text.getText().clear();

                                        InputMethodManager inputMethodManager = (InputMethodManager)
                                                NewsInfoActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                                                InputMethodManager.HIDE_NOT_ALWAYS);

                                        //评论成功，通过修改本地数据刷新界面
                                        if (communityCommentBean == null) {
                                            initRequest();
                                            eventPostBean = new EventPostBean();
                                            eventPostBean.setType(Config.NEWS_COMMENT_SUCCESS);
                                            eventPostBean.setId(newsInfoBean.getId());
                                            EventBus.getDefault().post(eventPostBean);
                                        } else {
                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId().equals(communityCommentBean.getId())) {
                                                    list.get(i).setCommetsum(Integer.parseInt(communityCommentBean.getCommetsum()) + 1 + "");
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(NewsInfoActivity.this, e.getMessage());
                                    }
                                });
                    }
                }
                return false;
            }
        });
    }

    private void initRequest() {
        OkGo.get(ApiRequestData.getInstance(this).PublishCommentList)
                .tag(this)
                .params("communityArticlesId", newsInfoBean.getId())
                .params("dataIndex",indexfrom)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(NewsInfoActivity.this).getDialogDismiss();
                        initRefresh();
                        NormalObjData<List<CommunityCommentBean>> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<List<CommunityCommentBean>>>() {
                                }.getType());
                        code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(NewsInfoActivity.this, mData.getMsg());
                            return;
                        }
                        if (mData.getData() == null || mData.getData().size() == 0) {
                            if (loadMore){
                                ToastUtil.show(NewsInfoActivity.this,"没有更多了！");
                                loadMore = false;
                                return;
                            }
                            setEmptyVisible(0);
                        } else {
                            if (list!=null&&list.size()>0){
                                list.clear();
                            }
                            list.addAll(mData.getData());
                            setEmptyVisible(list.size());
                            initdapter();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(NewsInfoActivity.this, e.getMessage());
                        initRefresh();
                    }
                });
    }

    private void initdapter() {
        //设置适配器
        if (adapter == null) {
            adapter = new FirstClassCommentAdapter(this, list, 4);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new FirstClassCommentAdapter.OnRecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    switch (view.getId()) {
                        case R.id.tv_reply:
                            if (TextUtil.isNull(userId)) {
                                LoadingDialog.showDialogForLogin(NewsInfoActivity.this);
                                return;
                            }
                            showSoftKeyboard(list.get(position));
                            break;
                    }
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void setEmptyVisible(int size) {
        if (size > 0) {
            view_empty.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            tv_comment_count.setText("全部评论(" + list.size() + ")");
        } else {
            view_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            iv_empty.setImageResource(R.mipmap.img_img_blank_comments);
            tv_empty_text.setText("暂无评论");
        }
    }

    private void initRefresh() {
        if (isRefresh) {
            swipe_layout.setRefreshing(false);
        } else {
            swipe_layout.setLoadingMore(false);
        }
    }


    @Override
    public void onRefresh() {
        isRefresh = true;
        if (list!=null&&list.size()>0){
            list.clear();
        }
        initRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        loadMore = true;
        initRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
        EventBus.getDefault().unregister(this);

        if (webapp != null) {
            webapp.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webapp.clearHistory();
            ((ViewGroup) webapp.getParent()).removeView(webapp);
            webapp.destroy();
            webapp = null;
        }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_SECOND_COMMENT_SUCCESS)
                ||eventPostBean.getType().equals(Config.LoginOut)) {
            userId = SPUtils.getSharedStringData(this, "userId");
            initRequest();
        }
    }
}

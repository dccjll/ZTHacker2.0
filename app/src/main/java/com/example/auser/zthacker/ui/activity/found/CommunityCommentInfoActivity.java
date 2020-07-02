package com.example.auser.zthacker.ui.activity.found;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FirstClassCommentAdapter;
import com.example.auser.zthacker.app.AppApplication;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.CommunityCommentBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.MyPublishDelDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.SoftKeyBoardListener;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.TimeUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.GlideCircleTransform;
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
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/10/31.
 */

public class CommunityCommentInfoActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.simpleDraweeView_head_icon)
    ImageView simpleDraweeView_head_icon;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_comment)
    TextView tv_comment;
    @BindView(R.id.tv_comment_more)
    TextView tv_comment_more;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_reply)
    TextView tv_reply;
    @BindView(R.id.tv_del_and_report)
    TextView tv_del_and_report;
    @BindView(R.id.tv_comment_count)
    TextView tv_comment_count;
    @BindView(R.id.view_empty)
    View view_empty;
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.rl_input_comment_text)
    RelativeLayout rl_input_comment_text;
    @BindView(R.id.input_comment_text)
    EditText input_comment_text;

    boolean isRefresh = false;
    private String inputtext;
    private CommunityCommentBean communityCommentBean;
    private String userId;
    private MyPublishDelDialog dialog;
    private GsonBuilder gsonBuilder;
    private EventPostBean eventPostBean;
    private List<CommunityCommentBean> list = new ArrayList<>();
    private String code;
    private FirstClassCommentAdapter adapter;
    public String communityCommentBeanId;

    /*
    * commentId 评论id
    * */
    public static void startActivity(Context context, CommunityCommentBean communityCommentBean) {
        Intent intent = new Intent(context, CommunityCommentInfoActivity.class);
        intent.putExtra("communityCommentBean", (Serializable) communityCommentBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_comment_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setCentreText("评论详情");
        userId = SPUtils.getSharedStringData(this, "userId");
        Intent intent = getIntent();
        communityCommentBean = (CommunityCommentBean) intent.getSerializableExtra("communityCommentBean");
        communityCommentBeanId = communityCommentBean.getId();
        Log.e("communityCommentBeanId", communityCommentBeanId);

        if (userId.equals(communityCommentBean.getSysAppUser())) {
            tv_reply.setVisibility(View.GONE);
        }
        tv_comment_more.setVisibility(View.GONE);
        if (communityCommentBean.getSysAppUser().equals(userId)) {
            tv_del_and_report.setText("删除");
        } else {
            tv_del_and_report.setText("举报");
        }
        tv_del_and_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communityCommentBean.getSysAppUser() != null &&
                        communityCommentBean.getSysAppUser().equals(userId)) {
                    removeDate(communityCommentBean, "删除", "");
                } else {
                    removeDate(communityCommentBean, "举报", "");
                }
            }
        });
        Glide.with(this)
                .load(communityCommentBean.getSysAppUerImg())
                .transform(new GlideCircleTransform(this))
                .placeholder(R.mipmap.icon_userphote_hl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .dontAnimate()
                .override(54, 54)
                .thumbnail(0.2f)
                .into(simpleDraweeView_head_icon);
        tv_comment.setText(communityCommentBean.getContent());
        tv_comment_count.setText("全部回复（" + communityCommentBean.getCommetsum() + "）");
        tv_name.setText(communityCommentBean.getUserName());
        tv_time.setText(TimeUtil.formatDateStr2Desc(communityCommentBean.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        view_empty.setVisibility(View.INVISIBLE);

        SoftKeyBoardListener.setListener(CommunityCommentInfoActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                input_comment_text.setFocusable(false);
                rl_input_comment_text.setVisibility(View.INVISIBLE);
            }
        });

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                2, getResources().getColor(R.color.part_divider_2)));
        recyclerView.setNestedScrollingEnabled(false);

        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();
    }

    public void setEmptyVisible(int size) {
        eventPostBean = new EventPostBean();
        eventPostBean.setType(Config.PUBLISH_SECOND_COMMENT_EMPTY);
        eventPostBean.setMessage(communityCommentBean.getId());
        eventPostBean.setCount(size);
        EventBus.getDefault().post(eventPostBean);
        tv_comment_count.setText("全部回复（" + size + "）");
        if (size == 0) {
            finish();
        }
    }

    private void removeDate(final CommunityCommentBean communityCommentBean, final String del, final String report) {
        dialog = (MyPublishDelDialog) LoadingDialog.showDialogForDelAndReport(this, del, report);
        this.dialog.setOnClickChoose(new MyPublishDelDialog.OnClickChoose() {
            @Override
            public void onClick(int id) {
                if (id == 1) {
                    if (TextUtil.isNull(userId)) {
                        LoadingDialog.showDialogForLogin(CommunityCommentInfoActivity.this);
                        return;
                    }
                    if (del.equals("删除")) {
                        ApiRequestData.getInstance(CommunityCommentInfoActivity.this).ShowDialog(null);
                        OkGo.get(ApiRequestData.getInstance(CommunityCommentInfoActivity.this).PublishCommentDel)//删除文章
                                .tag(CommunityCommentInfoActivity.this)
                                .params("id", communityCommentBean.getId())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(CommunityCommentInfoActivity.this).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                            ToastUtil.show(CommunityCommentInfoActivity.this, mData.getMsg());
                                            return;
                                        }
                                        eventPostBean = new EventPostBean();
                                        eventPostBean.setType(Config.PUBLISH_COMMENT_DEL);
                                        eventPostBean.setMessage(communityCommentBean.getId());
                                        EventBus.getDefault().post(eventPostBean);
                                        finish();
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(CommunityCommentInfoActivity.this, e.getMessage());
                                    }
                                });

                    } else if (del.equals("举报")) {
                        ReportActivity.startActivity(CommunityCommentInfoActivity.this, communityCommentBean.getSysAppUser(), communityCommentBean.getId(), 0);
                    }
                }
            }
        });
    }


    @OnClick({R.id.image_back, R.id.tv_del_and_report, R.id.tv_reply})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_del_and_report:
                ReportActivity.startActivity(this, communityCommentBean.getSysAppUser(), communityCommentBean.getId(), 0);
                break;
            case R.id.tv_reply:
                showSoftKeyboard(communityCommentBean);
                break;
        }
    }

    private void initRequest() {
        OkGo.get(ApiRequestData.getInstance(this).PublishSecondCommentList)
                .tag(this)
                .params("id", communityCommentBeanId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(CommunityCommentInfoActivity.this).getDialogDismiss();
                        initRefresh();
                        NormalObjData<List<CommunityCommentBean>> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<List<CommunityCommentBean>>>() {
                                }.getType());
                        code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                            ToastUtil.show(CommunityCommentInfoActivity.this, mData.getMsg());
                            return;
                        }
                        if (mData.getData() == null || mData.getData().size() == 0) {
                            ToastUtil.show(CommunityCommentInfoActivity.this, "没有更多了！");
                            return;
                        } else {
                            list.addAll(mData.getData());
                            initdapter();
                            tv_comment_count.setText("全部回复（" + list.size() + "）");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ApiRequestData.getInstance(CommunityCommentInfoActivity.this).getDialogDismiss();
                        ToastUtil.show(CommunityCommentInfoActivity.this, e.getMessage());
                        initRefresh();
                    }
                });
    }

    private void initdapter() {

        //设置适配器
        if (adapter == null) {
            adapter = new FirstClassCommentAdapter(this, list, 2);
            recyclerView.setAdapter(adapter);
            recyclerView.setFocusable(false);
            adapter.setOnItemClickListener(new FirstClassCommentAdapter.OnRecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    switch (view.getId()) {
                        case R.id.tv_reply:
                            showSoftKeyboard(list.get(position));
                            break;
                    }
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    public void showSoftKeyboard(final CommunityCommentBean communityCommentBean1) {
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
                        Toast.makeText(CommunityCommentInfoActivity.this, "请输入要发表的文字！", Toast.LENGTH_SHORT).show();
                    } else {
                        ApiRequestData.getInstance(CommunityCommentInfoActivity.this).ShowDialog(null);
                        OkGo.post(ApiRequestData.getInstance(CommunityCommentInfoActivity.this).PublishReplyComment)
                                .tag(this)
                                .params("communityArticlesId", AppApplication.getInstance().getCurrentArticleId())
                                .params("parentId", communityCommentBean1 == null ? "" : communityCommentBean.getId())
                                .params("sysAppUser", userId)
                                .params("sysAppUser2", communityCommentBean1 == null ? "" : communityCommentBean1.getSysAppUser())
                                .params("content", inputtext)
                                .params("relevantId", communityCommentBean1 == null ? "" : communityCommentBean1.getId())
                                .params(" commentType", communityCommentBean1 == null ? communityCommentBean.getCommentType():communityCommentBean1.getCommentType())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(CommunityCommentInfoActivity.this).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                            ToastUtil.show(CommunityCommentInfoActivity.this, mData.getMsg());
                                            return;
                                        }
                                        input_comment_text.getText().clear();

                                        InputMethodManager inputMethodManager = (InputMethodManager)
                                                CommunityCommentInfoActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                                                InputMethodManager.HIDE_NOT_ALWAYS);

                                        //评论成功，刷新界面
                                        initRequest();
                                        eventPostBean = new EventPostBean();
                                        eventPostBean.setType(Config.PUBLISH_SECOND_COMMENT_SUCCESS);
                                        EventBus.getDefault().post(eventPostBean);
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(CommunityCommentInfoActivity.this, e.getMessage());
                                    }
                                });
                    }
                }
                return false;
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
    public void onRefresh() {
        isRefresh = true;
        if (list != null && list.size() > 0) {
            list.clear();
        }
        initRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        if (list != null && list.size() > 0) {
            list.clear();
        }
        initRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }
}

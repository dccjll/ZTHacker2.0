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
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.MyPublishDelDialog;
import com.example.auser.zthacker.dialog.ShareDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.mine.UserHomePageActivity;
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
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Auser on 2017/10/31.
 */

public class CommunityPicInfoActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
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
    @BindView(R.id.banner_pictures)
    BGABanner banner_pictures;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.simpleDraweeView_head_icon)
    ImageView simpleDraweeView_head_icon;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_is_attention)
    TextView tv_is_attention;
    @BindView(R.id.tv_attention_count)
    TextView tv_attention_count;
    @BindView(R.id.iv_send)
    ImageView iv_send;
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
    private MyPublishDelDialog dialog;
    private boolean isRefresh = false;
    private PublishInfoList.PublishInfo publishInfo;
    private String inputtext;
    private GsonBuilder gsonBuilder;
    private String code;
    private List<CommunityCommentBean> list = new ArrayList<>();
    private FirstClassCommentAdapter adapter;
    private List<String> picturesList;
    private String userId;
    private EventPostBean eventPostBean;
    private int followCount;
    private int position;//当前图片的位置
    private ShareDialog shareDialog;
    /*private SharePlatform oks;*/
    private int indexfrom;
    private boolean loadMore = false;

    public static void startActivity(Context context, PublishInfoList.PublishInfo publishInfo,int position) {
        Intent intent = new Intent(context, CommunityPicInfoActivity.class);
        intent.putExtra("publishInfo", (Serializable) publishInfo);
        intent.putExtra("position",position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_pic_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setCentreText("详情");
        iv_send.setVisibility(View.VISIBLE);
        iv_send.setImageResource(R.mipmap.icon_threepoints);
        userId = SPUtils.getSharedStringData(this, "userId");
        publishInfo = (PublishInfoList.PublishInfo) getIntent().getSerializableExtra("publishInfo");
        position = getIntent().getIntExtra("position", 0);
        AppApplication.getInstance().setCurrentArticleId(publishInfo.getId());

        SoftKeyBoardListener.setListener(CommunityPicInfoActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
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

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                2, getResources().getColor(R.color.part_divider_2)));
        recyclerView.setNestedScrollingEnabled(false);

        ApiRequestData.getInstance(this).ShowDialog(null);
        initFollowCountRequest();

        if (TextUtil.isNull(publishInfo.getTitle())) {
            tv_title.setVisibility(View.GONE);
        } else {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(publishInfo.getTitle());
        }
        tv_time.setText(TimeUtil.formatDateStr2Desc(publishInfo.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        //tv_look_count.setText(publishInfo.getLikeCount()+"");
        Glide.with(this)
                .load(publishInfo.getSysAppUerImg())
                .transform(new GlideCircleTransform(this))
                .placeholder(R.mipmap.icon_userphote_hl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .dontAnimate()
                .override(54,54)
                .thumbnail(0.2f)
                .into(simpleDraweeView_head_icon);
        tv_name.setText(publishInfo.getSysAppUserName());
        if (TextUtil.isNull(publishInfo.getContent())) {
            tv_content.setVisibility(View.GONE);
        } else {
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(publishInfo.getContent());
        }
        tv_comment_count.setText("全部评论(" + publishInfo.getCommentCount() + ")");
        tv_attention_count.setText(publishInfo.getCollectCount() + "人关注");
        iv_zan.setImageResource(publishInfo.getIsLike() == 1 ? R.mipmap.bottombar_icon_zan_hl : R.mipmap.bottombar_icon_zan);
        tv_zan_count.setText(publishInfo.getLikeCount() + "");
        iv_collection.setImageResource(publishInfo.getIsCollect() == 1 ? R.mipmap.bottombar_icon_star_hl : R.mipmap.bottombar_icon_star);
        tv_collection_count.setText(publishInfo.getCollectCount() + "");
        /*iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        setAttentionBg(publishInfo.getIsFollow());
        if (publishInfo.getType().equals("1")) {
            banner_pictures.setVisibility(View.VISIBLE);
            banner_pictures.setAutoPlayAble(false);
            picturesList = publishInfo.getImagesList(publishInfo.getImgIds());
            banner_pictures.setData(picturesList, null);
            banner_pictures.setCurrentItem(position);
            banner_pictures.setAdapter(new BGABanner.Adapter<ImageView, String>() {
                @Override
                public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                    if (!TextUtil.isNull(model)) {
                        Glide.with(CommunityPicInfoActivity.this)
                                .load(model)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .skipMemoryCache(false)
                                .dontAnimate()
                                .placeholder(R.mipmap.img_placeholder_210)
                                .centerCrop()
                                .override(400,400)
                                .thumbnail(0.2f)
                                .into(itemView);
                    }
                }
            });
        } else {
            banner_pictures.setVisibility(View.GONE);
        }
    }

    public void setEmptyVisible(int size) {
        if (size > 0) {
            view_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            view_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            iv_empty.setImageResource(R.mipmap.img_img_blank_comments);
            tv_empty_text.setText("暂无评论");
        }
    }

    private void setAttentionBg(int isAttention) {
        if (publishInfo.getSysAppUser().equals(userId)) {
            tv_is_attention.setVisibility(View.GONE);
        }else {
            tv_is_attention.setVisibility(View.VISIBLE);
            if (isAttention == 1) {
                tv_is_attention.setText("已关注");
                tv_is_attention.setTextColor(getResources().getColor(R.color.text_64));
                tv_is_attention.setBackground(getResources().getDrawable(R.drawable.attention_bg));
            } else {
                tv_is_attention.setText("+关注");
                tv_is_attention.setTextColor(getResources().getColor(R.color.white));
                tv_is_attention.setBackground(getResources().getDrawable(R.drawable.register_bg));
            }
        }
    }

    private void removeDate(final PublishInfoList.PublishInfo publishInfo, final String del,final String report) {
        dialog = (MyPublishDelDialog) LoadingDialog.showDialogForDelAndReport(this, del,report);
        this.dialog.setOnClickChoose(new MyPublishDelDialog.OnClickChoose() {
            @Override
            public void onClick(int id) {
                if (id == 1) {
                    if (TextUtil.isNull(userId)) {
                        LoadingDialog.showDialogForLogin(CommunityPicInfoActivity.this);
                        return;
                    }
                    if (del.equals("删除")) {
                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).ShowDialog(null);
                        OkGo.get(ApiRequestData.getInstance(CommunityPicInfoActivity.this).PublishTextDel)//删除文章
                                .tag(CommunityPicInfoActivity.this)
                                .params("articleId", publishInfo.getId())
                                .params("sysAppUser", userId)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(code)) {
                                            ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                                            return;
                                        }
                                        eventPostBean = new EventPostBean();
                                        eventPostBean.setType(Config.PUBLISH_TEXT_DEL);
                                        eventPostBean.setMessage(publishInfo.getId());
                                        EventBus.getDefault().post(eventPostBean);
                                        finish();
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
                                    }
                                });
                    } else if (del.equals("举报")) {
                        ReportActivity.startActivity(CommunityPicInfoActivity.this, publishInfo.getSysAppUser(), publishInfo.getId(), 1);
                    }
                }
            }
        });
    }

    private void initCommentRequest() {
        Log.e("communityArticlesId",publishInfo.getId());
        OkGo.get(ApiRequestData.getInstance(this).PublishCommentList)
                .tag(this)
                .params("communityArticlesId", publishInfo.getId())
                .params("dataIndex",indexfrom)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                        initRefresh();
                        NormalObjData<List<CommunityCommentBean>> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<List<CommunityCommentBean>>>() {
                                }.getType());
                        code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                            ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                            return;
                        }
                        if (mData.getData() == null || mData.getData().size() == 0) {
                            if (loadMore){
                                ToastUtil.show(CommunityPicInfoActivity.this,"没有更多了！");
                                loadMore = false;
                                return;
                            }
                            setEmptyVisible(0);
                        } else {
                            if (indexfrom==0&&list!=null&&list.size()>0){
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
                        ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                        initRefresh();
                    }
                });
    }

    private void initFollowCountRequest() {
        OkGo.get(ApiRequestData.getInstance(this).PublishTextUserFollowCount)
                .tag(this)
                .params("sysAppUser", publishInfo.getSysAppUser())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        initCommentRequest();
                        NormalObjData<Integer> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<Integer>>() {
                                }.getType());
                        code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                            ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                            return;
                        }
                        followCount = mData.getData();
                        tv_attention_count.setText(followCount + "人关注");
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                        initRefresh();
                    }
                });
    }

    private void initdapter() {
        //设置适配器
        if (adapter == null) {
            adapter = new FirstClassCommentAdapter(this, list, 0);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new FirstClassCommentAdapter.OnRecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    switch (view.getId()) {
                        case R.id.tv_reply:
                            if (TextUtil.isNull(userId)) {
                                LoadingDialog.showDialogForLogin(CommunityPicInfoActivity.this);
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

    @OnClick({R.id.image_back, R.id.input_comment, R.id.ll_zan, R.id.ll_collection,R.id.simpleDraweeView_head_icon
              ,R.id.tv_is_attention,R.id.iv_send})
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
                        .params("objectId", publishInfo.getId())
                        .params("sysAppUser", userId)
                        .params("likeType", "1")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                                NormalObjData<UtilBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                        }.getType());
                                String code = mData.getCode();
                                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                    ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                                    return;
                                }
                                if (mData.getData() != null) {
                                    if (mData.getData().getState() == 0) {
                                        publishInfo.setLikeCount(publishInfo.getLikeCount() - 1);
                                    } else {
                                        publishInfo.setLikeCount(publishInfo.getLikeCount() + 1);
                                    }
                                    publishInfo.setIsLike(mData.getData().getState());
                                    tv_zan_count.setText(publishInfo.getLikeCount() + "");
                                    iv_zan.setImageResource(mData.getData().getState() == 0
                                            ? R.mipmap.bottombar_icon_zan : R.mipmap.bottombar_icon_zan_hl);
                                    refreshPublishList(1);
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
                                ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                            }
                        });
                break;
            case R.id.ll_collection:
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(this);
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.get(ApiRequestData.getInstance(this).PublishTextCollection)//收藏
                        .tag(this)
                        .params("communityArticleId", publishInfo.getId())
                        .params("sysAppUser", userId)
                        .params("likeType", "1")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                                NormalObjData<UtilBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                        }.getType());
                                String code = mData.getCode();
                                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                    ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                                    return;
                                }
                                if (mData.getData() != null) {
                                    if (mData.getData().getState() == 0) {
                                        publishInfo.setCollectCount(publishInfo.getCollectCount() - 1);
                                    } else {
                                        publishInfo.setCollectCount(publishInfo.getCollectCount() + 1);
                                    }
                                    Log.e("fafdsfsdfsd222", publishInfo.getCollectCount() + "");
                                    publishInfo.setIsCollect(mData.getData().getState());
                                    tv_collection_count.setText(publishInfo.getCollectCount() + "");
                                    iv_collection.setImageResource(mData.getData().getState() == 0
                                            ? R.mipmap.bottombar_icon_star : R.mipmap.bottombar_icon_star_hl);
                                    refreshPublishList(2);
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
                                ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                            }
                        });
                break;
            case R.id.tv_is_attention:
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(CommunityPicInfoActivity.this);
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.get(ApiRequestData.getInstance(this).PublishTextAttention)
                        .tag(this)
                        .params("sysAppUser", userId)
                        .params("sysAppUser2", publishInfo.getSysAppUser())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                                NormalObjData<UtilBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                        }.getType());
                                String code = mData.getCode();
                                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                    ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                                    return;
                                }
                                if (mData.getData() != null) {
                                    setAttentionBg(mData.getData().getState());
                                    if (mData.getData().getState()==1){
                                        followCount = followCount+1;
                                    }else {
                                        followCount = followCount-1;
                                    }
                                    tv_attention_count.setText(followCount + "人关注");
                                    publishInfo.setIsFollow(mData.getData().getState());
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.PERSONAL_ATTENTION);
                                    eventPostBean.setCount(publishInfo.getIsFollow());
                                    eventPostBean.setId(publishInfo.getSysAppUser());
                                    EventBus.getDefault().post(eventPostBean);
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
                                ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                            }
                        });
                break;
            case R.id.simpleDraweeView_head_icon:
                UserHomePageActivity.startActivity(this, publishInfo.getSysAppUser());
                break;
            case R.id.iv_send:
                if (!TextUtil.isNull(publishInfo.getSysAppUser()) && publishInfo.getSysAppUser().equals(userId)) {
                    removeDate(publishInfo, "删除","");
                } else {
                    removeDate(publishInfo, "举报","");
                }
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
                        oks.share("WechatMoments",publishInfo.getTitle()
                                ,publishInfo.getContent(),publishInfo.getImgIds(),"");
                    }

                    @Override
                    public void onWXHY() {
                        oks.share("Wechat",publishInfo.getTitle()
                                ,publishInfo.getContent(),publishInfo.getImgIds(),"");
                    }

                    @Override
                    public void onQQ() {
                        oks.share("QQ",publishInfo.getTitle()
                                ,publishInfo.getContent(),publishInfo.getImgIds(),"");
                    }

                    @Override
                    public void onSina() {
                        oks.share("SinaWeibo",publishInfo.getTitle()
                                ,publishInfo.getContent(),publishInfo.getImgIds(),"");
                    }
                });
                break;*/
        }
    }

   /* private void showShare() {
        if (picturesList!=null&&picturesList.size()>0){
            imageArray =  picturesList.toArray(new String[picturesList.size()]);
        }
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(publishInfo.getTitle());
        // titleUrl QQ和QQ空间跳转链接
        //oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(publishInfo.getContent());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if (publishInfo.getType().equals("1")){
            oks.setImageArray(imageArray);//确保SDcard下面存在此张图片
        }
        // url在微信、微博，Facebook等平台中使用
        //oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        // 启动分享GUI
        oks.show(this);
    }*/

    private void refreshPublishList(int type) {
        eventPostBean = new EventPostBean();
        if (type==1){
            eventPostBean.setType(Config.PUBLISH_ZAN);
        }else {
            eventPostBean.setType(Config.PUBLISH_COLLECTION);
        }
        eventPostBean.setPublishInfo(publishInfo);
        EventBus.getDefault().post(eventPostBean);
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
                        Toast.makeText(CommunityPicInfoActivity.this, "请输入要发表的文字！", Toast.LENGTH_SHORT).show();
                    } else {
                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).ShowDialog(null);
                        OkGo.post(ApiRequestData.getInstance(CommunityPicInfoActivity.this).PublishReplyComment)
                                .tag(this)
                                .params("communityArticlesId", publishInfo.getId())
                                .params("parentId", communityCommentBean == null ? "" : communityCommentBean.getId())
                                .params("sysAppUser", userId)
                                .params("sysAppUser2", communityCommentBean == null ? "" : communityCommentBean.getSysAppUser())
                                .params("content", inputtext)
                                .params("relevantId", communityCommentBean == null ? "" : communityCommentBean.getId())
                                .params(" commentType", "1")
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(CommunityPicInfoActivity.this).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                            ToastUtil.show(CommunityPicInfoActivity.this, mData.getMsg());
                                            return;
                                        }
                                        input_comment.getText().clear();
                                        input_comment_text.getText().clear();

                                        InputMethodManager inputMethodManager = (InputMethodManager)
                                                CommunityPicInfoActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                                                InputMethodManager.HIDE_NOT_ALWAYS);
                                        //评论成功，通过修改本地数据刷新界面
                                        if (communityCommentBean == null) {
                                            initCommentRequest();
                                            publishInfo.setCommentCount(publishInfo.getCommentCount()+1);
                                            tv_comment_count.setText("全部评论(" + publishInfo.getCommentCount() + ")");
                                            eventPostBean = new EventPostBean();
                                            eventPostBean.setType(Config.PUBLISH_COMMENT_SUCCESS);
                                            eventPostBean.setId(publishInfo.getId());
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
                                        ToastUtil.show(CommunityPicInfoActivity.this, e.getMessage());
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
        indexfrom = 0;
        if (list!=null&&list.size()>0){
            list.clear();
        }
        initCommentRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        loadMore = true;
        if (list!=null&&list.size()>0) {
            indexfrom = list.get(list.size() - 1).getDataIndex();
        }
        Log.e("indexfrom",indexfrom+"");
        initCommentRequest();
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_DEL)) {
            Iterator<CommunityCommentBean> iterator = list.iterator();
            while (iterator.hasNext()) {
                CommunityCommentBean next = iterator.next();
                if (next.getId().equals(eventPostBean.getMessage())) {
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
            publishInfo.setCommentCount(publishInfo.getCommentCount() - 1);
            tv_comment_count.setText("全部评论(" + publishInfo.getCommentCount() + ")");
            setEmptyVisible(publishInfo.getCommentCount());
        } else if (eventPostBean.getType().equals(Config.PUBLISH_SECOND_COMMENT_EMPTY)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(eventPostBean.getMessage())) {
                    list.get(i).setCommetsum(eventPostBean.getCount() + "");
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (eventPostBean.getType().equals(Config.PUBLISH_SECOND_COMMENT_SUCCESS)
            ||eventPostBean.getType().equals(Config.LoginOut)) {
            userId = SPUtils.getSharedStringData(this, "userId");
            initCommentRequest();
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
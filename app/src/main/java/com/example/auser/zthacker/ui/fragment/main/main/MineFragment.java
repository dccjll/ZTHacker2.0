package com.example.auser.zthacker.ui.fragment.main.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MsgCommentListBean;
import com.example.auser.zthacker.bean.MsgNoneWatchedCountBean;
import com.example.auser.zthacker.bean.MsgZanListBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.MainActivity;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;
import com.example.auser.zthacker.ui.activity.message.MyMessageActivity;
import com.example.auser.zthacker.ui.activity.mine.MyCollectionActivity;
import com.example.auser.zthacker.ui.activity.mine.MyFollowsActivity;
import com.example.auser.zthacker.ui.activity.mine.MyPublishActivity;
import com.example.auser.zthacker.ui.activity.mine.PersonalInfoActivity;
import com.example.auser.zthacker.ui.activity.mine.SettingActivity;
import com.example.auser.zthacker.ui.activity.mine.VideoManagerActivity;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.GlideCircleTransform;
import com.example.auser.zthacker.zxing.activity.CaptureActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/4.
 */

public class MineFragment extends BaseFragment implements OnRefreshListener {
    @BindView(R.id.ll_message)
    LinearLayout ll_message;
    @BindView(R.id.simpleDraweeView_head_icon)
    ImageView simpleDraweeView_head_icon;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    LinearLayout swipe_target;
    @BindView(R.id.tv_publish_count)
    TextView tv_publish_count;
    @BindView(R.id.tv_collection_count)
    TextView tv_collection_count;
    @BindView(R.id.tv_follow_count)
    TextView tv_follow_count;
    @BindView(R.id.tv_fans_count)
    TextView tv_fans_count;
    @BindView(R.id.iv_red_dot)
    ImageView iv_red_dot;
    private AppPersonalInfoBean personalInfo;
    private GsonBuilder gsonBuilder;
    private String userId;
    boolean isRefresh = false;
    private String code;
    private MsgNoneWatchedCountBean msgNoneWatchedCountBean;
    private int commentCount;
    private int likeCount;
    private int zanMsgIndex;
    private int commentMsgIndex;
    private boolean isFragmentVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_mine);
        EventBus.getDefault().register(this);
        return view;
    }

    protected void initView() {
        userId = SPUtils.getSharedStringData(getActivity(), "userId");
        commentCount = SPUtils.getSharedIntData(getActivity(),"commentCount");
        likeCount = SPUtils.getSharedIntData(getActivity(),"likeCount");
        zanMsgIndex = SPUtils.getSharedIntData(getActivity(),"zanMsgIndex");
        commentMsgIndex = SPUtils.getSharedIntData(getActivity(),"commentMsgIndex");
        if (commentCount + likeCount > 0) {
            iv_red_dot.setVisibility(View.VISIBLE);
        } else {
            iv_red_dot.setVisibility(View.GONE);
        }
        Log.e("userId", userId);
        personalInfo = ((MainActivity)getActivity()).personalInfo;
        setPersonalInfoView();
        if (personalInfo!=null) {
            initCommentRequest();
            initZanRequest();
            initMsgRequest();
        }
        swipe_layout.setOnRefreshListener(this);
    }


    private void initMsgRequest() {
        ApiRequestData.getInstance(getActivity()).getMsgCount(userId,zanMsgIndex,commentMsgIndex, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                if (isRefresh) {
                    swipe_layout.setRefreshing(false);
                }
                if (!isFirst) {
                    isFirst = true;
                }
                NormalObjData<MsgNoneWatchedCountBean> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<MsgNoneWatchedCountBean>>() {
                        }.getType());
                code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                    ToastUtil.show(getActivity(), mData.getMsg());
                    return;
                }
                msgNoneWatchedCountBean = mData.getData();
                Log.e("commentCount1", msgNoneWatchedCountBean.getCoummentCount() + "/" + msgNoneWatchedCountBean.getLikeCount());
                commentCount +=msgNoneWatchedCountBean.getCoummentCount();
                likeCount += msgNoneWatchedCountBean.getLikeCount();
                msgNoneWatchedCountBean.setLikeCount(likeCount);
                msgNoneWatchedCountBean.setCoummentCount(commentCount);
                SPUtils.setSharedIntData(getActivity(),"commentCount",commentCount);
                SPUtils.setSharedIntData(getActivity(),"likeCount",likeCount);
                Log.e("commentCount", commentCount + "/" + likeCount);
                if (commentCount + likeCount > 0) {
                    iv_red_dot.setVisibility(View.VISIBLE);
                } else {
                    iv_red_dot.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                ToastUtil.show(getActivity(), e.getMessage());
                if (isRefresh) {
                    swipe_layout.setRefreshing(false);
                }
            }
        });
    }

    private void initZanRequest() {
        ApiRequestData.getInstance(getActivity()).getMsgMyPublishZanList(userId, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                NormalObjData<List<MsgZanListBean>> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<List<MsgZanListBean>>>() {
                        }.getType());
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                    ToastUtil.show(getActivity(), mData.getMsg());
                    return;
                }
                zanMsgIndex = mData.getDataIndex();
                SPUtils.setSharedIntData(getActivity(),"zanMsgIndex",zanMsgIndex);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtil.show(getActivity(), e.getMessage());
            }
        });
    }

    private void initCommentRequest() {
        ApiRequestData.getInstance(getActivity()).getMsgPublishCommentList(userId, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                NormalObjData<MsgCommentListBean> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<MsgCommentListBean>>() {
                        }.getType());
                String code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                    ToastUtil.show(getActivity(), mData.getMsg());
                    return;
                }
                MsgCommentListBean msgCommentListBean = mData.getData();
                if (msgCommentListBean!=null&&msgCommentListBean.getCommentList()!=null
                        &&msgCommentListBean.getCommentList().size()>0){
                    commentMsgIndex = msgCommentListBean.getCommentList().get(0).getDataIndex();
                    SPUtils.setSharedIntData(getActivity(),"commentMsgIndex",commentMsgIndex);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                ToastUtil.show(getActivity(), e.getMessage());
            }
        });
    }

    public void initRequest() {
        OkGo.get(ApiRequestData.getInstance(getActivity()).MineInfo)// 请求方式和请求url
                .tag(this)
                .params("id", userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        if (isRefresh) {
                            swipe_layout.setRefreshing(false);
                        }
                        if (!isFirst) {
                            isFirst = true;
                        }
                        NormalObjData<AppPersonalInfoBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<AppPersonalInfoBean>>() {
                                }.getType());
                        personalInfo = mData.getData();
                        code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(getActivity(), mData.getMsg());
                            return;
                        }
                        SPUtils.setSharedStringData(getActivity(), "phone", personalInfo.getPhoneNumber());
                        setPersonalInfoView();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        ToastUtil.show(getActivity(), e.getMessage());
                        if (isRefresh) {
                            swipe_layout.setRefreshing(false);
                        }
                    }
                });
    }

    private void setPersonalInfoView() {
        if (personalInfo == null) {
            simpleDraweeView_head_icon.setImageResource(R.mipmap.icon_userphote_hl);
            tv_username.setText("未登录");
            tv_publish_count.setText("0");
            tv_collection_count.setText("0");
            tv_follow_count.setText("0");
            tv_fans_count.setText("0");
        } else {
            if (!TextUtil.isNull(personalInfo.getPhoto())) {
                Glide.with(this)
                        .load(personalInfo.getPhoto())
                        .transform(new GlideCircleTransform(getActivity()))
                        .placeholder(R.mipmap.icon_userphote_hl)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(96,96)
                        .thumbnail(0.2f)
                        .into(simpleDraweeView_head_icon);
            }
            if (TextUtil.isNull(personalInfo.getUserName())) {
                String phoneNumberEllipse = personalInfo.getPhoneNumber().substring(0, 3) + "*****" + personalInfo.getPhoneNumber().substring(8, 11);
                tv_username.setText(phoneNumberEllipse);
            } else {
                tv_username.setText(personalInfo.getUserName());
            }
            tv_publish_count.setText(personalInfo.getArticlesCount() + "");
            tv_collection_count.setText(personalInfo.getCollectCount() + "");
            tv_follow_count.setText(personalInfo.getFollowCount() + "");
            tv_fans_count.setText(personalInfo.getFollowMeCount() + "");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }
        if (view == null) {
            return;
        }
        if (isFragmentVisible){
           onFragmentVisibleChange(true);
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
            initZanRequest();
            initCommentRequest();
            initMsgRequest();
        } else {
            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
        }
    }

    @OnClick({R.id.ll_userinfo, R.id.ll_message, R.id.ll_scan, R.id.ll_setting, R.id.ll_my_video
            , R.id.ll_class_consult, R.id.ll_complaint_feedback, R.id.simpleDraweeView_head_icon, R.id.ll_publish
            , R.id.ll_collection, R.id.ll_follow, R.id.ll_fans})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_message:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    MyMessageActivity.startActivity(getActivity(), msgNoneWatchedCountBean);
                }
                break;
            case R.id.ll_userinfo:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    PersonalInfoActivity.startActivity(getActivity(), personalInfo);
                }
                break;
            case R.id.ll_scan:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    CaptureActivity.startActivity(getActivity());
                }
                break;
            case R.id.ll_my_video:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    VideoManagerActivity.startActivity(getActivity());
                }
                break;
            case R.id.ll_class_consult:
                ClassConsultActivity.startActivity(getActivity(), Config.CLASS_CONSULT);
                break;
            case R.id.ll_complaint_feedback:
                ClassConsultActivity.startActivity(getActivity(), Config.COMPLAINT_FEEDBACK);
                break;
            case R.id.ll_setting:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    SettingActivity.startActivity(getActivity());
                }
                break;
            case R.id.simpleDraweeView_head_icon:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                }
                break;
            case R.id.ll_publish:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    MyPublishActivity.startActivity(getActivity());
                }
                break;
            case R.id.ll_collection:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    MyCollectionActivity.startActivity(getActivity());
                }
                break;
            case R.id.ll_follow:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    MyFollowsActivity.startActivity(getActivity(), 0);
                }
                break;
            case R.id.ll_fans:
                if (personalInfo == null || TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                } else {
                    MyFollowsActivity.startActivity(getActivity(), 1);
                }
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.EDIT_USERNAME)
                || eventPostBean.getType().equals(Config.EDIT_USERPHOTO)
                || eventPostBean.getType().equals(Config.EDIT_USERSEX)
                || eventPostBean.getType().equals(Config.EDIT_USERIDENTITY)) {
            initRequest();
        } else if (eventPostBean.getType().equals(Config.LoginOut)) {
            userId = eventPostBean.getMessage();
            if (TextUtil.isNull(eventPostBean.getMessage())) {
                personalInfo = null;
                setPersonalInfoView();
                iv_red_dot.setVisibility(View.GONE);
            } else {
                initMsgRequest();
                initCommentRequest();
                initZanRequest();
                initRequest();
            }
        } else if (eventPostBean.getType().equals(Config.PUBLISH_ZAN)
                ||eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)
                ||eventPostBean.getType().equals(Config.PUBLISH_IMAGE)
                ||eventPostBean.getType().equals(Config.PERSONAL_ATTENTION)) {
            initRequest();
        } else if (eventPostBean.getType().equals(Config.MSG_NOTICE)){
            commentCount = SPUtils.getSharedIntData(getActivity(),"commentCount");
            likeCount = SPUtils.getSharedIntData(getActivity(),"likeCount");
            msgNoneWatchedCountBean.setCoummentCount(commentCount);
            msgNoneWatchedCountBean.setLikeCount(likeCount);
            if (commentCount + likeCount > 0) {
                iv_red_dot.setVisibility(View.VISIBLE);
            } else {
                iv_red_dot.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        initRequest();
        initZanRequest();
        initCommentRequest();
        initMsgRequest();
    }
}

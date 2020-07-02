package com.example.auser.zthacker.ui.fragment.main.course;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.TeachingResourseAdapter;
import com.example.auser.zthacker.adapter.VideoListAdapter;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.VideoInfoBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.course.CourseInfoIntroActivity;
import com.example.auser.zthacker.ui.activity.home.ConceptActivity;
import com.example.auser.zthacker.ui.activity.home.CourseAndResourseActivity;
import com.example.auser.zthacker.ui.activity.home.WpsDisplayActivity;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/24.
 */

public class CourseChildFragment extends BaseFragment {
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.swipe_target)
    RecyclerView rcv;
    public static final String ARG_PAGE = "ARG_PAGE";
    private String url;
    private VideoListAdapter adapter;
    private GsonBuilder gsonBuilder;
    private String code;
    private List<VideoInfoBean> list;
    private TeachingResourseAdapter teachingAdapter;
    private String userId;
    private String courseId;
    private List<HomeClassTypeInfoBean> classlist;
    boolean isRefresh = false;
    private boolean haveLongVideo;

    public static CourseChildFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, url);
        CourseChildFragment pageFragment = new CourseChildFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_course_child);
        //initView();
        return view;
    }

    protected void initView() {
        userId = SPUtils.getSharedStringData(getActivity(), "userId");
        courseId = ((CourseInfoIntroActivity) getActivity()).courseInfoBean.getId();

        if (url.equals("1") || url.equals("2")) {
            webview.setVisibility(View.GONE);
            rcv.setVisibility(View.VISIBLE);
            if (url.equals("2")) {
                rcv.setBackgroundColor(getActivity().getResources().getColor(R.color.color_co_50));
            } else {
                rcv.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
            rcv.setLayoutManager(linearLayoutManager);
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
            initMyClassListRequest();
        } else {
            webview.setVisibility(View.VISIBLE);
            rcv.setVisibility(View.GONE);
            //设置WebView属性，能够执行Javascript脚本
            webview.getSettings().setJavaScriptEnabled(true);
            //加载需要显示的网页
            webview.loadUrl(url);
        }
    }

    private void initMyClassListRequest() {
        if (!TextUtil.isNull(userId)) {
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
            OkGo.post(ApiRequestData.getInstance(getActivity()).MineMyClass)
                    .tag(this)
                    .params("appUserId", userId)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (gsonBuilder == null) {
                                gsonBuilder = new GsonBuilder();
                            }
                            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                            NormalObjData<List<HomeClassTypeInfoBean>> mData = gsonBuilder
                                    .setPrettyPrinting()
                                    .disableHtmlEscaping()
                                    .create().fromJson(s, new TypeToken<NormalObjData<List<HomeClassTypeInfoBean>>>() {
                                    }.getType());
                            code = mData.getCode();
                            if (!TextUtil.isNull(code) && code.equals("0")) {
                                ToastUtil.show(getActivity(), mData.getMsg());
                            }
                            classlist = mData.getData();
                            if (classlist != null && classlist.size() > 0) {
                                haveLongVideo = false;
                                for (int i = 0; i < classlist.size(); i++) {
                                    if (classlist.get(i).getId().equals(courseId)) {
                                        haveLongVideo = true;
                                        break;
                                    }
                                }
                                if (haveLongVideo) {
                                    getLongVideoListRequest();
                                } else {
                                    getShortVideoListRequest();
                                }
                            } else {
                                getShortVideoListRequest();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                            ToastUtil.show(getActivity(), e.getMessage());
                        }
                    });
        } else {
            getShortVideoListRequest();
        }
    }

    private void getShortVideoListRequest() {
        OkGo.post(ApiRequestData.getInstance(getActivity()).MineMyClassVideoList)
                .tag(this)
                .params("courseId", ((CourseInfoIntroActivity) getActivity()).courseInfoBean.getId())
                .params("type", "0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        // initRefresh();
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        NormalObjData<List<VideoInfoBean>> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<List<VideoInfoBean>>>() {
                                }.getType());
                        code = mData.getCode();
                        list = mData.getData();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(getActivity(), mData.getMsg());
                            return;
                        }
                        if (list != null) {
                            if (url.equals("1")) {
                                initVideoAdapter();
                            } else if (url.equals("2")) {
                                initTeachingResourseAdapter();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(getActivity(), e.getMessage());
                        // initRefresh();
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                    }
                });
    }

    private void getLongVideoListRequest() {
        OkGo.post(ApiRequestData.getInstance(getActivity()).MineMyClassVideoList)
                .tag(this)
                .params("courseId", courseId)
                .params("type", "1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        // initRefresh();
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        NormalObjData<List<VideoInfoBean>> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<List<VideoInfoBean>>>() {
                                }.getType());
                        code = mData.getCode();
                        list = mData.getData();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(getActivity(), mData.getMsg());
                            return;
                        }
                        if (list != null) {
                            if (url.equals("1")) {
                                initVideoAdapter();
                            } else if (url.equals("2")) {
                                initTeachingResourseAdapter();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //initRefresh();
                        ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                        ToastUtil.show(getActivity(), e.getMessage());
                    }
                });
    }

    private void initTeachingResourseAdapter() {
        if (teachingAdapter == null) {
            teachingAdapter = new TeachingResourseAdapter(getActivity(), list);
            rcv.setAdapter(teachingAdapter);
            teachingAdapter.setOnItemClickListener(new TeachingResourseAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!EasyPermissions.hasPermissions(getActivity(), perms)) {
                        EasyPermissions.requestPermissions(getActivity(), "需要访问手机存储权限！", 10086, perms);
                    } else {
                        WpsDisplayActivity.startActivity(getActivity(), list.get(position).getCourseWarePPTUrl(), list.get(position).getCourseWareName());
                    }
                }
            });
        } else {
            teachingAdapter.notifyDataSetChanged();
        }
    }

    private void initVideoAdapter() {
        if (adapter == null) {
            adapter = new VideoListAdapter(getActivity(), list);
            rcv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
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
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

   /* @Override
    public void onRefresh() {
        isRefresh = true;
        if (haveLongVideo){
            if (url.equals("https://video/1")||url.equals(Config.VIDEO_ABOUT_TEACHING_RESOURSE)){
                getLongVideoListRequest();
            }
        }else {
            getShortVideoListRequest();
        }
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        if (haveLongVideo){
            if (url.equals("https://video/1")||url.equals(Config.VIDEO_ABOUT_TEACHING_RESOURSE)){
                getLongVideoListRequest();
            }
        }else {
            getShortVideoListRequest();
        }
    }

    private void initRefresh() {
        if (isRefresh) {
            swipe_layout.setRefreshing(false);
        } else {
            swipe_layout.setLoadingMore(false);
        }
    }*/
}

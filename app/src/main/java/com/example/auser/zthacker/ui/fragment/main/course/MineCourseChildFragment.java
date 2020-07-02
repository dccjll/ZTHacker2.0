package com.example.auser.zthacker.ui.fragment.main.course;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.VideoInfoBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.course.CourseInfoIntroActivity;
import com.example.auser.zthacker.ui.activity.home.WpsDisplayActivity;
import com.example.auser.zthacker.ui.activity.mine.VideoManagerListActivity;
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
 * Created by Dell on 2018-2-9.
 */

public class MineCourseChildFragment extends BaseFragment {
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
    private String courseId;

    public static MineCourseChildFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, url);
        MineCourseChildFragment pageFragment = new MineCourseChildFragment();
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
        webview.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        rcv.setLayoutManager(linearLayoutManager);

        courseId = ((VideoManagerListActivity) getActivity()).courseId;
        getLongVideoListRequest();
    }

    private void getLongVideoListRequest() {
        if (!TextUtil.isNull(courseId)) {
            ApiRequestData.getInstance(getActivity()).ShowDialog(null);
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
                            Log.e("courseId",courseId);
                            // initRefresh();
                            ApiRequestData.getInstance(getActivity()).getDialogDismiss();
                            NormalObjData<List<VideoInfoBean>> mData = gsonBuilder
                                    .setPrettyPrinting()
                                    .disableHtmlEscaping()
                                    .create().fromJson(s, new TypeToken<NormalObjData<List<VideoInfoBean>>>() {
                                    }.getType());
                            code = mData.getCode();
                            list = mData.getData();
                            if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                ToastUtil.show(getActivity(), mData.getMsg());
                                return;
                            }
                            if (list != null) {
                                Log.e("list",list.toString());
                                if (url.equals(Config.VIDEO)) {
                                    initVideoAdapter();
                                } else if (url.equals(Config.VIDEO_ABOUT_TEACHING_RESOURSE)) {
                                    initTeachingResourseAdapter();
                                }
                            }
                            Log.e("list1","122124");
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
}

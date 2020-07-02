package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyClassAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.zxing.activity.CaptureActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/16.
 */

public class VideoManagerActivity extends BaseActivity{
    @BindView(R.id.tv_scan)
    TextView tv_scan;
    @BindView(R.id.rcv)
    RecyclerView rcv;
    private String userId;
    private GsonBuilder gsonBuilder;
    private List<HomeClassTypeInfoBean> list;
    private MyClassAdapter adapter;
    private String code;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,VideoManagerActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_manager);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("我的视频");
        userId = SPUtils.getSharedStringData(this, "userId");

        rcv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        initRequest();
    }

    private void initRequest() {
        ApiRequestData.getInstance(this).ShowDialog(null);
        OkGo.post(ApiRequestData.getInstance(this).MineMyClass)
                .tag(this)
                .params("appUserId",userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder==null){
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(VideoManagerActivity.this).getDialogDismiss();
                        NormalObjData<List<HomeClassTypeInfoBean>> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<List<HomeClassTypeInfoBean>>>(){}.getType());
                        code = mData.getCode();
                        list = mData.getData();
                        if (list!=null){
                            initAdapter();
                        }else {
                            rcv.setVisibility(View.GONE);
                        }
                        if (!TextUtil.isNull(code)&&code.equals("0")){
                            ToastUtil.show(VideoManagerActivity.this,mData.getMsg());
                        }
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ApiRequestData.getInstance(VideoManagerActivity.this).getDialogDismiss();
                        ToastUtil.show(VideoManagerActivity.this,e.getMessage());
                    }
                });
    }

    private void initAdapter() {
        if (adapter==null){
            adapter = new MyClassAdapter(this,list);
            rcv.setAdapter(adapter);
            adapter.setOnItemClickListener(new MyClassAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    VideoManagerListActivity.startActivity(VideoManagerActivity.this,list.get(position).getId());
                }
            });
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.image_back,R.id.tv_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_scan:
                CaptureActivity.startActivity(this);
                break;
        }
    }
    @Subscribe
    public void onEventMainThread(String msg) {
        if (msg.equals(Config.SCAN_SUCCESS)){
            rcv.setVisibility(View.VISIBLE);
            initRequest();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        OkGo.getInstance().cancelTag(this);
    }
}

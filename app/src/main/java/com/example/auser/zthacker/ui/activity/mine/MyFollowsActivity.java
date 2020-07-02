package com.example.auser.zthacker.ui.activity.mine;

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
import com.example.auser.zthacker.adapter.MyFollowsAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.UserFollowBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017-11-17.
 */

public class MyFollowsActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;
    boolean isRefresh = false;
    private int type;
    private String userId;
    private GsonBuilder gsonBuilder;
    private List<UserFollowBean> followList = new ArrayList<>();
    private MyFollowsAdapter adapter;

    /*
    * type:类型 0-关注 1-粉丝
    * */
    public static void startActivity(Context context,int type){
        Intent intent = new Intent(context,MyFollowsActivity.class);
        intent.putExtra("type",type);
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
        type = getIntent().getIntExtra("type", 0);
        if (type==0){
            setCentreText("我的关注");
        }else if (type==1){
            setCentreText("粉丝");
        }
        userId = SPUtils.getSharedStringData(this, "userId");
        recyclerView.setPadding(0,0,0,0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();
    }

    private void initRequest() {
        switch (type){
            case 0:
                OkGo.post(ApiRequestData.getInstance(this).MineFollows)
                        .tag(this)
                        .params("sysAppUser", userId)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                initRefresh();
                                NormalObjData<List<UserFollowBean>> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<List<UserFollowBean>>>() {
                                        }.getType());
                                ApiRequestData.getInstance(MyFollowsActivity.this).getDialogDismiss();
                                String code = mData.getCode();
                                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                    ToastUtil.show(MyFollowsActivity.this, mData.getMsg());
                                    return;
                                }
                                if (followList!=null){
                                    followList.clear();
                                }
                                if (mData.getData()!=null&&mData.getData().size()>0){
                                    followList.addAll(mData.getData());
                                }
                                initAdapter();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(MyFollowsActivity.this, e.getMessage());
                                initRefresh();
                            }
                        });
                break;
            case 1:
                OkGo.post(ApiRequestData.getInstance(this).MineFans)
                        .tag(this)
                        .params("sysAppUser", userId)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder == null) {
                                    gsonBuilder = new GsonBuilder();
                                }
                                initRefresh();
                                NormalObjData<List<UserFollowBean>> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<List<UserFollowBean>>>() {
                                        }.getType());
                                ApiRequestData.getInstance(MyFollowsActivity.this).getDialogDismiss();
                                String code = mData.getCode();
                                if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                    ToastUtil.show(MyFollowsActivity.this, mData.getMsg());
                                    return;
                                }
                                if (followList!=null){
                                    followList.clear();
                                }
                                if (mData.getData()!=null&&mData.getData().size()>0) {
                                    followList.addAll(mData.getData());
                                }
                                initAdapter();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.show(MyFollowsActivity.this, e.getMessage());
                                initRefresh();
                            }
                        });
                break;
        }
    }

    private void initAdapter() {
       if (followList==null||followList.size()==0){
           iv_empty.setVisibility(View.VISIBLE);
           tv_empty_text.setVisibility(View.VISIBLE);
           recyclerView.setVisibility(View.GONE);
           if (type==0){
               iv_empty.setImageResource(R.mipmap.img_blankpage_myfans);
               tv_empty_text.setText("暂无关注");
           }else if (type==1){
               iv_empty.setImageResource(R.mipmap.img_blankpage_myfans);
               tv_empty_text.setText("暂无粉丝");
           }
           tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
       }else {
           iv_empty.setVisibility(View.GONE);
           tv_empty_text.setVisibility(View.GONE);
           recyclerView.setVisibility(View.VISIBLE);
           //设置适配器
           if (adapter==null){
               adapter = new MyFollowsAdapter(this,followList,type);
               recyclerView.setAdapter(adapter);
           }else {
               adapter.notifyDataSetChanged();
           }
       }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
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

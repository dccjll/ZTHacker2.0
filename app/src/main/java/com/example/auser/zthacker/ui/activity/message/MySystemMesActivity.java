package com.example.auser.zthacker.ui.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MySystemAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017-11-16.
 */

public class MySystemMesActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    RecyclerView swipe_target;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;

    boolean isRefresh = false;
    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRefresh) {
                swipe_layout.setRefreshing(false);
            }else {
                swipe_layout.setLoadingMore(false);
            }
            handler.removeCallbacks(this);// 关闭定时器处理
        }
    };
    private MySystemAdapter adapter;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,MySystemMesActivity.class);
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
        setCentreText("系统消息");
        initAdapter();
    }

    private void initAdapter() {
        /*swipe_target.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        //设置适配器
        adapter = new MySystemAdapter(this);
        swipe_target.setAdapter(adapter);*/

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);

        if (adapter ==null){
            iv_empty.setImageResource(R.mipmap.img_blankpage_message);
            tv_empty_text.setText("暂无系统消息");
            tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        handler.postDelayed(runnable, 1500);// 打开定时器，执行操作
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        handler.postDelayed(runnable, 1500);// 打开定时器，执行操作
    }

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }
}

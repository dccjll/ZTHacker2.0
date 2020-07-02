package com.example.auser.zthacker.ui.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyMsgAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MsgNoneWatchedCountBean;
import com.example.auser.zthacker.utils.SPUtils;
import com.lzy.okgo.OkGo;
import java.io.Serializable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * Created by zhengkq on 2017/8/16.
 */

public class MyMessageActivity extends BaseActivity{
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.swipe_target)
    RecyclerView swipe_target;

    private MsgNoneWatchedCountBean msgNoneWatchedCountBean;
    private MyMsgAdapter adapter;

    public static void startActivity(Context context, MsgNoneWatchedCountBean msgNoneWatchedCountBean){
        Intent intent = new Intent(context,MyMessageActivity.class);
        intent.putExtra("msgNoneWatchedCountBean",(Serializable) msgNoneWatchedCountBean);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("我的消息");
        swipe_layout.setLoadMoreEnabled(false);
        swipe_layout.setRefreshEnabled(false);
        //zanMsgIndex = SPUtils.getSharedIntData(this,"zanMsgIndex");
        //commentMsgIndex = SPUtils.getSharedIntData(this,"commentMsgIndex");
        Intent intent = getIntent();
        msgNoneWatchedCountBean = (MsgNoneWatchedCountBean) intent.getSerializableExtra("msgNoneWatchedCountBean");

        swipe_target.setPadding(0,0,0,0);
        swipe_target.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        initAdapter();
    }

    private void initAdapter() {
        //设置适配器
        if (adapter==null){
            adapter = new MyMsgAdapter(this,msgNoneWatchedCountBean);
            swipe_target.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

   /* private void initRequest() {
        ApiRequestData.getInstance(this).getMsgCount(userId,zanMsgIndex,commentMsgIndex, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder==null){
                    gsonBuilder = new GsonBuilder();
                }
                ApiRequestData.getInstance(MyMessageActivity.this).getDialogDismiss();
                initRefresh();
                if (isRefresh){
                    swipe_layout.setRefreshing(false);
                }
                NormalObjData<MsgNoneWatchedCountBean> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<MsgNoneWatchedCountBean>>(){}.getType());
                String code = mData.getCode();
                if (!TextUtil.isNull(code)&&code.equals("0")&&!TextUtil.isNull(mData.getMsg())){
                    ToastUtil.show(MyMessageActivity.this,mData.getMsg());
                    return;
                }
                MsgNoneWatchedCountBean data = mData.getData();
                msgNoneWatchedCountBean.setCoummentCount(msgNoneWatchedCountBean.getCoummentCount()+data.getCoummentCount());
                msgNoneWatchedCountBean.setLikeCount(msgNoneWatchedCountBean.getLikeCount()+data.getLikeCount());
                initAdapter();
            }
            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ApiRequestData.getInstance(MyMessageActivity.this).getDialogDismiss();
                ToastUtil.show(MyMessageActivity.this,e.getMessage());
                initRefresh();
            }
        });
    }*/

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.MSG_NOTICE)){
            int commentCount = SPUtils.getSharedIntData(MyMessageActivity.this,"commentCount");
            int likeCount = SPUtils.getSharedIntData(MyMessageActivity.this,"likeCount");
            msgNoneWatchedCountBean.setCoummentCount(commentCount);
            msgNoneWatchedCountBean.setLikeCount(likeCount);
            initAdapter();
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

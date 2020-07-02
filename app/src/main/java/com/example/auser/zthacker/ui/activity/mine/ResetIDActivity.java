package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.ProfessionalSelectAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.TypeSelectBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/7.
 */

public class ResetIDActivity extends BaseActivity {
    @BindView(R.id.image_back)
    LinearLayout image_back;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_complete)
    TextView tv_complete;

    private List<TypeSelectBean> list;
    private String identity;
    private ProfessionalSelectAdapter adapter;
    private GsonBuilder gsonBuilder;
    private EventPostBean eventPostBean;
    private String code;

    public static void startActivity(Context context,String identity){
        Intent intent = new Intent(context,ResetIDActivity.class);
        intent.putExtra("identity",identity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_sex);
        ButterKnife.bind(this);
        setTop(R.color.black);
        setCentreText("修改身份");
        initView();
    }

    private void initView() {
        identity = getIntent().getStringExtra("identity");
        list = new ArrayList<>();
        if(TextUtil.isNull(identity)){
            list.add(new TypeSelectBean("老师", true));
            list.add(new TypeSelectBean("学生",false));
            list.add(new TypeSelectBean("爱好者",false));
            list.add(new TypeSelectBean("机构",false));
            list.add(new TypeSelectBean("其他",false));
        }else {
            list.add(new TypeSelectBean("老师",identity.equals("老师")));
            list.add(new TypeSelectBean("学生",identity.equals("学生")));
            list.add(new TypeSelectBean("爱好者",identity.equals("爱好者")));
            list.add(new TypeSelectBean("机构",identity.equals("机构")));
            list.add(new TypeSelectBean("其他",identity.equals("其他")));
        }
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecycleViewDividerLine(this,RecycleViewDividerLine.HORIZONTAL_LIST,
                2,getResources().getColor(R.color.part_divider_2)));
        //设置适配器
        if (adapter==null){
            adapter = new ProfessionalSelectAdapter(this, list);
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new ProfessionalSelectAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0;i<list.size();i++){
                    if (i==position){
                        list.get(i).setSelect(true);
                    }else {
                        list.get(i).setSelect(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.image_back,R.id.tv_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_complete:
                for (int i = 0;i<list.size();i++){
                    if (list.get(i).isSelect()){
                        identity = list.get(i).getName();
                    }
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).MineInfoEdit)// 请求方式和请求url
                        .tag(this)
                        .params("id", SPUtils.getSharedStringData(this,"userId"))
                        .params("identity",identity)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(ResetIDActivity.this).getDialogDismiss();
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(ResetIDActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&& code.equals("1")){
                                    ToastUtil.show(ResetIDActivity.this,"修改成功！");
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.EDIT_USERIDENTITY);
                                    eventPostBean.setMessage(identity);
                                    EventBus.getDefault().post(eventPostBean);
                                    finish();
                                }else {
                                    ToastUtil.show(ResetIDActivity.this,mData.getMsg());
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ResetIDActivity.this).getDialogDismiss();
                                ToastUtil.show(ResetIDActivity.this,e.getMessage());
                            }
                        });
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

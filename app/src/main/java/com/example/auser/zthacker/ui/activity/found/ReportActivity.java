package com.example.auser.zthacker.ui.activity.found;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.ProfessionalSelectAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.bean.TypeSelectBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.MainActivity;
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
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/10/31.
 * 举报
 */

public class ReportActivity extends BaseActivity {
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.tv_word_count)
    TextView tv_word_count;
    private ProfessionalSelectAdapter adapter;
    private String inputText;
    private String sysAppUser;
    private int type;
    private String userId;
    private String reasons;
    private GsonBuilder gsonBuilder;
    private String reportObject;

    /*
    * sysAppUser 被投诉人id
    * type 类型（0-评论 1-文章）
    * */
    public static void startActivity(Context context,String sysAppUser,String reportObject,int type) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra("sysAppUser",sysAppUser);
        intent.putExtra("reportObject",reportObject);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("举报");
        tv_edit.setText("提交");
        userId = SPUtils.getSharedStringData(this, "userId");
        Intent intent = getIntent();
        sysAppUser = intent.getStringExtra("sysAppUser");
        reportObject = intent.getStringExtra("reportObject");
        type = intent.getIntExtra("type", -1);
        final List<TypeSelectBean> list = new ArrayList<>();
        list.add(new TypeSelectBean("内容低俗", true));
        list.add(new TypeSelectBean("包含政治", false));
        list.add(new TypeSelectBean("这是广告", false));
        list.add(new TypeSelectBean("内容虚假", false));
        reasons = list.get(0).getName();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                2, getResources().getColor(R.color.part_divider_2)));
        //设置适配器
        if (adapter == null) {
            adapter = new ProfessionalSelectAdapter(this, list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new ProfessionalSelectAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == position) {
                        list.get(i).setSelect(true);
                        reasons = list.get(i).getName();
                    } else {
                        list.get(i).setSelect(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputText = et_input.getText().toString();
                tv_word_count.setText(inputText.length()+"/140");
            }
        });
    }

    @OnClick({R.id.image_back, R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_edit:
                inputText = et_input.getText().toString();
                ApiRequestData.getInstance(this).ShowDialog(null);
                initRequest();
                break;
        }
    }

    private void initRequest() {
        OkGo.get(ApiRequestData.getInstance(this).PublishTextReport)
                .tag(this)
                .params("loginSysAppUser",userId)
                .params("sysAppUser",sysAppUser)
                .params("reasons",reasons)
                .params("content",inputText)
                .params("reportObject",reportObject)
                .params("type",type+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder==null){
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(ReportActivity.this).getDialogDismiss();
                        NormalObjData mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                        String code = mData.getCode();
                        ToastUtil.show(ReportActivity.this, mData.getMsg());
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            return;
                        }
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(ReportActivity.this, e.getMessage());
                        ApiRequestData.getInstance(ReportActivity.this).getDialogDismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }
}

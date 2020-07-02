package com.example.auser.zthacker.ui.activity.found;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.HomeData;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishTextBean;
import com.example.auser.zthacker.bean.TypeSelectBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.MyPublishDelDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.search.SearchInputActivity;
import com.example.auser.zthacker.ui.activity.search.SearchListActivity;
import com.example.auser.zthacker.utils.LocalSearchUtils;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.FlowLayout;
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
 * Created by zhengkq on 2017/8/10.
 */

public class PublishTextActivity extends BaseActivity {
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.et_input_title)
    EditText et_input_title;
    @BindView(R.id.et_input_text)
    EditText et_input_text;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.tv_fl)
    TextView tv_fl;
    @BindView(R.id.fl)
    FlowLayout fl;
    private long firstClick;
    private List<String> hotTip;
    private String inputContent;
    private String inputTitle;
    private String userId;
    private GsonBuilder gsonBuilder;
    private MyPublishDelDialog dialog;
    private PublishTextBean publishTextBean;
    private String code;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PublishTextActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_text);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("发布");
        tv_edit.setText("发布");

        tv_fl.setVisibility(View.GONE);
        fl.setVisibility(View.GONE);

        userId = SPUtils.getSharedStringData(PublishTextActivity.this, "userId");
        et_input_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputContent = et_input_text.getText().toString();
                tv_count.setText(inputContent.length() + "");
                tv_count.setTextColor(inputContent.length() > 0 ? getResources().getColor(R.color.delete_bg) : getResources().getColor(R.color.text_c0));
            }
        });

        hotTip = new ArrayList<>();
        hotTip.add("航模");
        hotTip.add("车模");
        hotTip.add("海模");
        hotTip.add("建模");
        setFlowlayout();
    }

    private void setFlowlayout() {
        for (int i = 0; i < hotTip.size(); i++) {
            int left, top, right, bottom;
            left = top = right = bottom = (int) getResources().getDimension(R.dimen.x5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(left, top, right, bottom);
            final TextView mView = new TextView(this);

            mView.setText(hotTip.get(i));
            mView.setTextSize(14);
            if (i == 0) {
                mView.setTextColor(getResources().getColor(R.color.white));
                mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_select_bg));
            } else {
                mView.setTextColor(getResources().getColor(R.color.text_c0));
                mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_unselect_bg));
            }
            mView.setTag(i);

            mView.setPadding(2 * left, 2 * left / 3, 2 * left, 2 * left / 3);
            mView.setLayoutParams(params);

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(mView);
            fl.addView(ll);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    for (int i = 0;i<fl.getChildCount();i++){
                        LinearLayout linearLayout = (LinearLayout) fl.getChildAt(i);
                        TextView childText = (TextView) linearLayout.getChildAt(0);
                        if ((int)childText.getTag()==tag){
                            childText.setTextColor(getResources().getColor(R.color.white));
                            childText.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_select_bg));
                        }else {
                            childText.setTextColor(getResources().getColor(R.color.text_c0));
                            childText.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_unselect_bg));
                        }
                    }
                }
            });
        }
    }

    @OnClick({R.id.image_back, R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                dialog = (MyPublishDelDialog) LoadingDialog.showDialogForDelAndReport(this,"放弃发布","");
                dialog.setOnClickChoose(new MyPublishDelDialog.OnClickChoose() {
                    @Override
                    public void onClick(int id) {
                        if (id==1){
                            finish();
                        }
                    }
                });
                break;
            case R.id.tv_edit:
                if (TextUtil.isNull(userId)){
                    LoadingDialog.showDialogForLogin(this);
                    return;
                }
                long secondClick = System.currentTimeMillis();
                if (secondClick - firstClick < 1000) {
                    firstClick = secondClick;
                    return;
                }
                inputContent = et_input_text.getText().toString();
                inputTitle = et_input_title.getText().toString();
                if (inputContent.length() == 0) {
                    Toast.makeText(PublishTextActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog("正在发布...");
                OkGo.post(ApiRequestData.getInstance(this).PublishText)
                        .tag(this)
                        .params("sysAppUser",userId)
                        .params("title",inputTitle)
                        .params("content",inputContent)
                        .params("type","0")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(PublishTextActivity.this).getDialogDismiss();
                                NormalObjData<PublishTextBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<PublishTextBean>>(){}.getType());
                                publishTextBean = mData.getData();
                                code = mData.getCode();
                                ToastUtil.show(PublishTextActivity.this,mData.getMsg());
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    return;
                                }
                                if (publishTextBean!=null){
                                    EventPostBean eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.PUBLISH_IMAGE);
                                    EventBus.getDefault().post(eventPostBean);
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(PublishTextActivity.this).getDialogDismiss();
                                ToastUtil.show(PublishTextActivity.this,e.getMessage());
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

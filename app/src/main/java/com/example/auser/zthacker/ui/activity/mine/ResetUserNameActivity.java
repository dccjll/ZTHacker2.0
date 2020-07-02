package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/7.
 */

public class ResetUserNameActivity extends BaseActivity{
    @BindView(R.id.image_back)
    LinearLayout image_back;
    @BindView(R.id.tv_complete)
    TextView tv_complete;
    @BindView(R.id.et_input_name)
    TextView et_input_name;
    private String input;
    private GsonBuilder gsonBuilder;
    private EventPostBean eventPostBean;
    private String code;
    private String userName;

    public static void startActivity(Context context,String userName){
        Intent intent = new Intent(context,ResetUserNameActivity.class);
        intent.putExtra("userName",userName);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_username);
        ButterKnife.bind(this);
        setTop(R.color.black);
        setCentreText("修改用户名");
        userName = getIntent().getStringExtra("userName");
        if (!TextUtil.isNull(userName)){
            et_input_name.setText(userName);
            tv_complete.setBackground(getResources().getDrawable(R.drawable.register_bg));
        }else {
            tv_complete.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
        }
        initView();
    }

    private void initView() {
        et_input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input = et_input_name.getText().toString().trim();
                if (input.length()>=2){
                    tv_complete.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_complete.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
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
                input = et_input_name.getText().toString().trim();
                if (input.length()<2){
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).MineInfoEdit)// 请求方式和请求url
                        .tag(this)
                        .params("id", SPUtils.getSharedStringData(this,"userId"))
                        .params("userName",input)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(ResetUserNameActivity.this).getDialogDismiss();
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(ResetUserNameActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&& code.equals("1")){
                                    ToastUtil.show(ResetUserNameActivity.this,"保存成功！");
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.EDIT_USERNAME);
                                    eventPostBean.setMessage(input);
                                    EventBus.getDefault().post(eventPostBean);
                                    finish();
                                }else {
                                    ToastUtil.show(ResetUserNameActivity.this,mData.getMsg());
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ResetUserNameActivity.this).getDialogDismiss();
                                ToastUtil.show(ResetUserNameActivity.this,e.getMessage());
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

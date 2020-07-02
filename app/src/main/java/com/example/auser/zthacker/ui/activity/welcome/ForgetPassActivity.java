package com.example.auser.zthacker.ui.activity.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.ValidatorUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/4.
 */

public class ForgetPassActivity extends BaseActivity{
    @BindView(R.id.image_back)
    LinearLayout image_back;
    @BindView(R.id.tv_captcha)
    TextView tv_captcha;
    @BindView(R.id.input_number)
    EditText input_number;
    @BindView(R.id.input_captcha)
    EditText input_captcha;
    @BindView(R.id.tv_next)
    TextView tv_next;

    private int recLen;
    private String inputPhone;
    private boolean isSendCaptcha = true;
    private long lastTimeMillis;
    private long currentTimeMillis;
    private String captcha;
    private GsonBuilder gsonBuilder;
    private String code;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,ForgetPassActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("找回密码");
        lastTimeMillis = SPUtils.getSharedlongData(this,"currentTimeMillisU");
        recLen = SPUtils.getSharedIntData(this,"recLenU");
        currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTimeMillis >=recLen*1000){
            canSendWithText();
        }else {
            recLen = recLen - (int)((currentTimeMillis - lastTimeMillis)/1000);
            uncanSendWithText();
            isSendCaptcha= false;
        }
        input_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputPhone = input_number.getText().toString().trim();
                captcha = input_captcha.getText().toString().trim();
                if (inputPhone.length()==11&&captcha.length()==6){
                    tv_next.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_next.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
        input_captcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputPhone = input_number.getText().toString().trim();
                captcha = input_captcha.getText().toString().trim();
                if (inputPhone.length()==11&&captcha.length()==6){
                    tv_next.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_next.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
    }

    @OnClick({R.id.image_back,R.id.tv_next,R.id.tv_captcha})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                SPUtils.setSharedlongData(this,"currentTimeMillisU",System.currentTimeMillis());
                SPUtils.setSharedIntData(this,"recLenU",recLen);
                finish();
                break;
            case R.id.tv_captcha:
                if (!isSendCaptcha) {
                    return;
                }
                inputPhone = input_number.getText().toString();
                if(TextUtil.isNull(inputPhone)){
                    ToastUtil.show(ForgetPassActivity.this,"手机号码不能为空！");
                    return;
                }
                if (!ValidatorUtil.isMobile(inputPhone)){
                    ToastUtil.show(ForgetPassActivity.this,"请输入正确的手机号格式！");
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).SendCaptcha)
                        .tag(this)
                        .params("mobile",inputPhone)
                        .params("type","U")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                ApiRequestData.getInstance(ForgetPassActivity.this).getDialogDismiss();
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(ForgetPassActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&& code.equals("1")){
                                    ToastUtil.show(ForgetPassActivity.this,"验证码发送成功！");
                                    sendPhone();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ForgetPassActivity.this).getDialogDismiss();
                                ToastUtil.show(ForgetPassActivity.this,e.getMessage());
                            }
                        });
                break;
            case R.id.tv_next:
                inputPhone = input_number.getText().toString().trim();
                captcha = input_captcha.getText().toString().trim();
                if(TextUtil.isNull(inputPhone)){
                    ToastUtil.show(ForgetPassActivity.this,"手机号码不能为空！");
                    return;
                }
                if (!ValidatorUtil.isMobile(inputPhone)){
                    ToastUtil.show(ForgetPassActivity.this,"请输入正确的手机号格式！");
                    return;
                }
                if (TextUtil.isNull(captcha)){
                    ToastUtil.show(ForgetPassActivity.this,"验证码不能为空！");
                    return;
                }
                if (captcha.length()!=6){
                    ToastUtil.show(ForgetPassActivity.this,"请输入6位验证码！");
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).CheckCaptcha)
                       .tag(this)
                        .params("phoneNumber",inputPhone)
                        .params("verCode",captcha)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                ApiRequestData.getInstance(ForgetPassActivity.this).getDialogDismiss();
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(ForgetPassActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&& code.equals("1")){
                                    ResetNewPassActivity.startActivity(ForgetPassActivity.this,inputPhone);
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ForgetPassActivity.this).getDialogDismiss();
                                ToastUtil.show(ForgetPassActivity.this,e.getMessage());
                            }
                        });
                break;
        }
    }

    private void sendPhone() {
        recLen = 60;
        isSendCaptcha = false;
        uncanSendWithText();//设置倒计时
    }

    private void uncanSendWithText() {
        tv_captcha.setTextColor(getResources().getColor(R.color.color_co_50));
        tv_captcha.setBackground(getResources().getDrawable(R.drawable.register_edit_bg));
        tv_captcha.setText("重新发送(" + recLen + "s)");
        Message message = handler.obtainMessage(1);
        handler.sendMessageDelayed(message, 1000);
    }

    private void canSendWithText() {
        tv_captcha.setTextColor(getResources().getColor(R.color.send_captcha_bg));
        tv_captcha.setText("获取验证码");
        tv_captcha.setBackground(getResources().getDrawable(R.drawable.captcha_bg));
        isSendCaptcha = true;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case 1:
                    recLen--;
                    if (recLen > 0) {
                        uncanSendWithText();
                    } else {
                        canSendWithText();
                    }
            }
            super.handleMessage(msg);
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SPUtils.setSharedlongData(this,"currentTimeMillisU",System.currentTimeMillis());
            SPUtils.setSharedIntData(this,"recLenU",recLen);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}

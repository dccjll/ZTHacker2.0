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
import android.widget.ImageView;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.StringSHA1;
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
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/3.
 */

public class RegisterActivity extends BaseActivity{
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.tv_captcha)
    TextView tv_captcha;
    @BindView(R.id.input_number)
    EditText input_number;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.input_captcha)
    EditText input_captcha;
    @BindView(R.id.tv_register)
    TextView tv_register;
    private boolean isSendCaptcha = true;
    private String inputPhone;
    private String inputPass;
    private GsonBuilder gsonBuilder;
    private AppPersonalInfoBean personalInfo;
    private EventPostBean eventPostBean;
    private String code;
    private int recLen;
    private long currentTimeMillis;
    private long lastTimeMillis;
    private String inputCaptcha;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
       setTop(R.color.black);
        lastTimeMillis = SPUtils.getSharedlongData(this,"currentTimeMillisR");
        recLen = SPUtils.getSharedIntData(this,"recLenR");
        currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis-lastTimeMillis>=recLen*1000){
            canSendWithText();
        }else {
            recLen = recLen - (int)((currentTimeMillis-lastTimeMillis)/1000);
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
                inputPhone = input_number.getText().toString();
                inputPass = input_password.getText().toString();
                inputCaptcha = input_captcha.getText().toString().trim();
                if (inputPhone.length()==11&&inputPass.length()>=6&&inputCaptcha.length()==6){
                    tv_register.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_register.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
        input_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputPhone = input_number.getText().toString().trim();
                inputPass = input_password.getText().toString().trim();
                inputCaptcha = input_captcha.getText().toString().trim();
                if (inputPhone.length()==11&&inputPass.length()>=6&&inputCaptcha.length()==6){
                    tv_register.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_register.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
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
                inputPass = input_password.getText().toString().trim();
                inputCaptcha = input_captcha.getText().toString().trim();
                if (inputPhone.length()==11&&inputPass.length()>=6&&inputCaptcha.length()==6){
                    tv_register.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_register.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
    }

    @OnClick({R.id.iv_close,R.id.tv_captcha,R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                SPUtils.setSharedlongData(this,"currentTimeMillisR",System.currentTimeMillis());
                SPUtils.setSharedIntData(this,"recLenR",recLen);
                finish();
                break;
            case R.id.tv_captcha:
                if (!isSendCaptcha) {
                    return;
                }
                inputPhone = input_number.getText().toString();
                if(TextUtil.isNull(inputPhone)){
                    ToastUtil.show(RegisterActivity.this,"手机号码不能为空！");
                    return;
                }
                if (!ValidatorUtil.isMobile(inputPhone)){
                    ToastUtil.show(RegisterActivity.this,"请输入正确的手机号格式！");
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).SendCaptcha)
                        .tag(this)
                        .params("mobile",inputPhone)
                        .params("type","R")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                ApiRequestData.getInstance(RegisterActivity.this).getDialogDismiss();
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&&code.equals("0")){
                                    ToastUtil.show(RegisterActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&&code.equals("1")){
                                    ToastUtil.show(RegisterActivity.this,"验证码发送成功！");
                                    sendPhone();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(RegisterActivity.this).getDialogDismiss();
                                ToastUtil.show(RegisterActivity.this,e.getMessage());
                            }
                        });

                break;
            case R.id.tv_register:
                inputPhone = input_number.getText().toString().trim();
                inputPass = input_password.getText().toString().trim();
                inputCaptcha = input_captcha.getText().toString().trim();
                if (inputPhone.length()<11||inputPass.length()<6||inputCaptcha.length()!=6){
                    return;
                }
                if (!ValidatorUtil.isMobile(inputPhone)){
                    ToastUtil.show(RegisterActivity.this,"请输入正确的手机号格式！");
                    return;
                }
                if (!ValidatorUtil.isPassword(inputPass)){
                    ToastUtil.show(RegisterActivity.this,"请输入6-18位数字与字母的密码！");
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).Register)
                        .tag(this)
                        .params("phoneNumber",inputPhone)
                        .params("password", StringSHA1.stringToMD5(inputPass))
                        .params("verCode",inputCaptcha)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(RegisterActivity.this).getDialogDismiss();
                                NormalObjData<AppPersonalInfoBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<AppPersonalInfoBean>>(){}.getType());
                                personalInfo = mData.getData();
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&&code.equals("0")){
                                    ToastUtil.show(RegisterActivity.this,mData.getMsg());
                                    return;
                                }
                                if (personalInfo==null){
                                    ToastUtil.show(RegisterActivity.this,mData.getMsg());
                                }else {
                                    SPUtils.setSharedStringData(RegisterActivity.this,"userId",personalInfo.getId());
                                    SPUtils.setSharedStringData(RegisterActivity.this,"pwd",inputPass);
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.LoginOut);
                                    eventPostBean.setMessage(personalInfo.getId());
                                    EventBus.getDefault().post(eventPostBean);
                                    LoadingDialog.cancelDialogForLogin();
                                    finish();
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(RegisterActivity.this).getDialogDismiss();
                                ToastUtil.show(RegisterActivity.this,e.getMessage());
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

    private void canSendWithText() {
        tv_captcha.setTextColor(getResources().getColor(R.color.send_captcha_bg));
        tv_captcha.setText("获取验证码");
        tv_captcha.setBackground(getResources().getDrawable(R.drawable.captcha_bg));
        isSendCaptcha = true;
    }

    private void uncanSendWithText() {
        tv_captcha.setTextColor(getResources().getColor(R.color.color_co_50));
        tv_captcha.setBackground(getResources().getDrawable(R.drawable.register_edit_bg));
        tv_captcha.setText("重新发送(" + recLen + "s)");
        Message message = handler.obtainMessage(1);
        handler.sendMessageDelayed(message, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SPUtils.setSharedlongData(this,"currentTimeMillisR",System.currentTimeMillis());
            SPUtils.setSharedIntData(this,"recLenR",recLen);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}

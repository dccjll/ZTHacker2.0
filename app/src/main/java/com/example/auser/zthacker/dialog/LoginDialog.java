package com.example.auser.zthacker.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.welcome.ForgetPassActivity;
import com.example.auser.zthacker.ui.activity.welcome.RegisterActivity;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.StringSHA1;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.ValidatorUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/7.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private EditText input_number;
    private EditText input_password;
    private String password;
    private String number;
    private TextView tv_login;
    private GsonBuilder gsonBuilder;
    private AppPersonalInfoBean personalInfo;
    private EventPostBean eventPostBean;
    private String code;

    public LoginDialog(@NonNull Context context) {
        super(context, R.style.HeadSelectDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarView();
        //setContentView(R.layout.dialog_login);

    }

    public void setView(View view){
        setContentView(view);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_forget_pass).setOnClickListener(this);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        input_number = (EditText) findViewById(R.id.input_number);
        input_password = (EditText) findViewById(R.id.input_password);
        input_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = input_password.getText().toString().trim();
                number = input_number.getText().toString().trim();
                if (number.length()==11&&password.length()>=6){
                    tv_login.setTextColor(getContext().getResources().getColor(R.color.send_captcha_bg));
                    tv_login.setBackground(getContext().getResources().getDrawable(R.drawable.login_bg));
                }else {
                    tv_login.setTextColor(getContext().getResources().getColor(R.color.white));
                    tv_login.setBackground(getContext().getResources().getDrawable(R.drawable.confirm_gray_bg));
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
                password = input_password.getText().toString().trim();
                number = input_number.getText().toString().trim();
                if (number.length()==11&&password.length()>=6){
                    tv_login.setTextColor(getContext().getResources().getColor(R.color.send_captcha_bg));
                    tv_login.setBackground(getContext().getResources().getDrawable(R.drawable.login_bg));
                }else {
                    tv_login.setBackground(getContext().getResources().getDrawable(R.drawable.confirm_gray_bg));
                    tv_login.setBackground(getContext().getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
    }

    private  void setStatusBarView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上系统
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  //全屏的意思，也就是会将状态栏隐藏
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //保留系统状态栏
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_register:
                RegisterActivity.startActivity(getContext());
                break;
            case R.id.tv_forget_pass:
                ForgetPassActivity.startActivity(getContext());
                break;
            case R.id.tv_login:
                password = input_password.getText().toString().trim();
                number = input_number.getText().toString().trim();
                if (number.length()<11||password.length()<6){
                    return;
                }
                if (!ValidatorUtil.isMobile(number)){
                    ToastUtil.show(getContext(),"请输入正确的手机号格式！");
                    return;
                }
                if (!ValidatorUtil.isPassword(password)){
                    ToastUtil.show(getContext(),"请输入6-18位数字与字母的密码！");
                    return;
                }

                //LoadingDialog.showDialogForLoading(context);
                ApiRequestData.getInstance(context).ShowDialog(null);
                Log.e("gfdsgsgs",StringSHA1.stringToMD5(password));
                OkGo.post( ApiRequestData.getInstance(context).Login)
                        .tag(this)
                        .params("phoneNumber",number)
                        .params("password", StringSHA1.stringToMD5(password))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(context).getDialogDismiss();
                                //LoadingDialog.cancelDialogForLoading();
                                NormalObjData<AppPersonalInfoBean> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<AppPersonalInfoBean>>(){}.getType());
                                personalInfo = mData.getData();
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(getContext(),mData.getMsg());
                                    return;
                                }
                                if (personalInfo!=null){
                                    Log.e("userId",personalInfo.getId());
                                    SPUtils.setSharedStringData(getContext(),"userId", personalInfo.getId());
                                    SPUtils.setSharedStringData(getContext(),"pwd",password);
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.LoginOut);
                                    eventPostBean.setMessage(personalInfo.getId());
                                    EventBus.getDefault().post(eventPostBean);
                                    dismiss();
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(context).getDialogDismiss();
                                //LoadingDialog.cancelDialogForLoading();
                                ToastUtil.show(context,e.getMessage());
                            }
                        });
                break;
        }
    }
}

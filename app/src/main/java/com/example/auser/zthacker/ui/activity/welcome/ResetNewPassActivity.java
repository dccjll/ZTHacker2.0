package com.example.auser.zthacker.ui.activity.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.StringSHA1;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
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
 * Created by zhengkq on 2017/8/7.
 */

public class ResetNewPassActivity extends BaseActivity{
    @BindView(R.id.image_back)
    LinearLayout image_back;
    @BindView(R.id.input_new_pass)
    EditText input_new_pass;
    @BindView(R.id.input_confirm_pass)
    EditText input_confirm_pass;
    @BindView(R.id.tv_next)
    TextView tv_next;
    private String phone;
    private String newPass;
    private String confirmPass;
    private GsonBuilder gsonBuilder;
    private String code;

    public static void startActivity(Context context,String phone){
        Intent intent = new Intent(context,ResetNewPassActivity.class);
        intent.putExtra("phone",phone);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_newpass);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("设置新密码");
        phone = getIntent().getStringExtra("phone");
        input_new_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newPass = input_new_pass.getText().toString().trim();
                confirmPass = input_confirm_pass.getText().toString().trim();
                if (newPass.length()>=6&&confirmPass.length()>=6){
                    tv_next.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_next.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
        input_confirm_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newPass = input_new_pass.getText().toString().trim();
                confirmPass = input_confirm_pass.getText().toString().trim();
                if (newPass.length()>=6&&confirmPass.length()>=6){
                    tv_next.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_next.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
    }

    @OnClick({R.id.image_back,R.id.tv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_next:
                newPass = input_new_pass.getText().toString().trim();
                confirmPass = input_confirm_pass.getText().toString().trim();
                if (newPass.length()<6||confirmPass.length()<6){
                    return;
                }
                if (!newPass.equals(confirmPass)){
                    ToastUtil.show(ResetNewPassActivity.this,"两次输入的密码不一致!");
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).ResetPass)
                        .tag(this)
                        .params("phoneNumber",phone)
                        .params("newPwd", StringSHA1.stringToMD5(newPass))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                ApiRequestData.getInstance(ResetNewPassActivity.this).getDialogDismiss();
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(ResetNewPassActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&& code.equals("1")){
                                    ToastUtil.show(ResetNewPassActivity.this,"密码修改成功！");
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ResetNewPassActivity.this).getDialogDismiss();
                                ToastUtil.show(ResetNewPassActivity.this,e.getMessage());
                            }
                        });
                finish();
                break;
        }
    }
}

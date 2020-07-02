package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/8.
 */

public class ChangePassActivity extends BaseActivity{
    @BindView(R.id.input_old_pass)
    EditText input_old_pass;
    @BindView(R.id.input_new_pass)
    EditText input_new_pass;
    @BindView(R.id.tv_next)
    TextView tv_next;
    private GsonBuilder gsonBuilder;
    private String code;
    private String oldPass;
    private String newPass;
    private String pwd;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,ChangePassActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("修改密码");
        pwd = SPUtils.getSharedStringData(ChangePassActivity.this, "pwd");
        input_old_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                oldPass = input_old_pass.getText().toString().trim();
                newPass = input_new_pass.getText().toString().trim();
                if (oldPass.length()>=6&&newPass.length()>=6){
                    tv_next.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_next.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
        input_new_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                oldPass = input_old_pass.getText().toString().trim();
                newPass = input_new_pass.getText().toString().trim();
                if (oldPass.length()>=6&&newPass.length()>=6){
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
                oldPass = input_old_pass.getText().toString().trim();
                newPass = input_new_pass.getText().toString().trim();
                if (oldPass.length()<6||newPass.length()<6){
                    return;
                }
                if (!ValidatorUtil.isPassword(oldPass)){
                    ToastUtil.show(ChangePassActivity.this,"请输入6-18位数字与字母的密码！");
                    return;
                }
                if (!pwd.equals(oldPass)){
                    ToastUtil.show(ChangePassActivity.this,"原密码输入不正确！");
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).MineChangePass)// 请求方式和请求url
                        .tag(this)
                        .params("id", SPUtils.getSharedStringData(this,"userId"))
                        .params("newPwd", StringSHA1.stringToMD5(newPass))
                        .params("oldPwd",StringSHA1.stringToMD5(oldPass))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(ChangePassActivity.this).getDialogDismiss();
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                code = mData.getCode();
                                if (!TextUtil.isNull(code)&& code.equals("0")){
                                    ToastUtil.show(ChangePassActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(code)&& code.equals("1")){
                                    ToastUtil.show(ChangePassActivity.this,"修改成功！");
                                    finish();
                                }else {
                                    ToastUtil.show(ChangePassActivity.this,mData.getMsg());
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ChangePassActivity.this).getDialogDismiss();
                                ToastUtil.show(ChangePassActivity.this,e.getMessage());
                            }
                        });
                break;
        }
    }
}

package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.ValidatorUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/8.
 */

public class ClassConsultActivity extends BaseActivity{
    @BindView(R.id.image_back)
    LinearLayout image_back;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.tv_telephone)
    TextView tv_telephone;
    @BindView(R.id.tv_qq)
    TextView tv_qq;
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.et_number)
    EditText et_number;
    @BindView(R.id.ll_bottom)
    LinearLayout ll_bottom;
    private String classType;
    private String input_content;
    private String input_number;
    private GsonBuilder gsonBuilder;
    private String phone;
    private String userId;

    public static void startActivity(Context context,String classType){
        Intent intent = new Intent(context,ClassConsultActivity.class);
        intent.putExtra("classType",classType);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_centre);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        Intent intent = getIntent();
        classType = intent.getStringExtra("classType");
        userId = SPUtils.getSharedStringData(this,"userId");
        phone = SPUtils.getSharedStringData(this,"phone");
        if (classType.equals(Config.CLASS_CONSULT)){
            setCentreText("课程咨询");
            et_input.setHint("请输入您想咨询的内容...");
            if (!TextUtil.isNull(this.userId)&&!TextUtil.isNull(phone)){
                et_number.setText(phone);
            }
        }else if (classType.equals(Config.COMPLAINT_FEEDBACK)){
            setCentreText("意见反馈");
            et_input.setHint("请输入您的反馈，我们将不断为您改进...");
            et_number.setVisibility(View.GONE);
            ll_bottom.setVisibility(View.GONE);
        }
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_content = et_input.getText().toString().trim();
                input_number = et_number.getText().toString().trim();
                if (classType.equals(Config.CLASS_CONSULT)){
                    if (input_content.length()>0&&input_number.length()==11){
                        tv_confirm.setBackground(getResources().getDrawable(R.drawable.register_bg));
                    }else {
                        tv_confirm.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                    }
                }else {
                    if (input_content.length()>0){
                        tv_confirm.setBackground(getResources().getDrawable(R.drawable.register_bg));
                    }else {
                        tv_confirm.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                    }
                }
            }
        });
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_content = et_input.getText().toString().trim();
                input_number = et_number.getText().toString().trim();
                if (input_content.length()>0&&input_number.length()==11){
                    tv_confirm.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_confirm.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
    }

    @OnClick({R.id.image_back,R.id.tv_confirm,R.id.ll_telephone, R.id.ll_qq})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_confirm:
                input_content = et_input.getText().toString().trim();
                String url;
                if (input_content.length()==0){
                    return;
                }
                if (classType.equals(Config.CLASS_CONSULT)){
                    input_number = et_number.getText().toString().trim();
                    if (input_number.length()!=11){
                        return;
                    }
                    if (!ValidatorUtil.isMobile(input_number)){
                        ToastUtil.show(ClassConsultActivity.this,"请输入正确的手机号！");
                        return;
                    }
                }else {
                    input_number = "";
                }
                url = ApiRequestData.getInstance(this).MineConsultSuggestion;
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(url)// 请求方式和请求url
                        .tag(this)
                        .params("appUserId", SPUtils.getSharedStringData(this,"userId"))
                        .params("content",input_content)
                        .params("phone",input_number)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(ClassConsultActivity.this).getDialogDismiss();
                                NormalObjData mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData>(){}.getType());
                                if (mData.isResult()){
                                    ToastUtil.show(ClassConsultActivity.this,"提交成功！");
                                    finish();
                                }else {
                                    ToastUtil.show(ClassConsultActivity.this,mData.getMsg());
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ClassConsultActivity.this).getDialogDismiss();
                                ToastUtil.show(ClassConsultActivity.this,e.getMessage());
                            }
                        });
                break;
            case R.id.ll_telephone:
                String telephone = tv_telephone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.ll_qq:
                boolean qqClientAvailable = ValidatorUtil.isQQClientAvailable(this);
                if (!qqClientAvailable){
                    ToastUtil.show(this,"请先安装QQ");
                    return;
                }
                String qqurl="mqqwpa://im/chat?chat_type=wpa&uin=3247843400";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqurl)));
                break;
        }
    }
}

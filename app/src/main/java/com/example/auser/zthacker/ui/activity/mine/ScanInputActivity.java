package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.VideoInfoBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/22.
 */

public class ScanInputActivity extends BaseActivity{
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    private String input;
    private String userId;
    private GsonBuilder gsonBuilder;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,ScanInputActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_input);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("扫一扫");
        userId = SPUtils.getSharedStringData(this, "userId");
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input = et_input.getText().toString().trim();
                if (input.length()>0){
                    tv_confirm.setBackground(getResources().getDrawable(R.drawable.register_bg));
                }else {
                    tv_confirm.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
                }
            }
        });
    }

    @OnClick({R.id.image_back,R.id.tv_back,R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_confirm:
                input = et_input.getText().toString().trim();
                if (TextUtil.isNull(input)){
                    return;
                }
                ApiRequestData.getInstance(this).ShowDialog(null);
                OkGo.post(ApiRequestData.getInstance(this).MineScanCode)
                        .tag(this)
                        .params("appUserId",userId)
                        .params("scancode",input)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (gsonBuilder==null){
                                    gsonBuilder = new GsonBuilder();
                                }
                                ApiRequestData.getInstance(ScanInputActivity.this).getDialogDismiss();
                                NormalObjData<String> mData = gsonBuilder
                                        .setPrettyPrinting()
                                        .disableHtmlEscaping()
                                        .create().fromJson(s, new TypeToken<NormalObjData<String>>(){}.getType());

                                if (!TextUtil.isNull(mData.getCode())&&mData.getCode().equals("0")){
                                    ToastUtil.show(ScanInputActivity.this,mData.getMsg());
                                    return;
                                }
                                if (!TextUtil.isNull(mData.getCode())&&mData.getCode().equals("1")&&!TextUtil.isNull(mData.getData())){
                                    EventBus.getDefault().post(Config.SCAN_SUCCESS);
                                    VideoManagerListActivity.startActivity(ScanInputActivity.this,mData.getData());
                                    finish();
                                }
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ApiRequestData.getInstance(ScanInputActivity.this).getDialogDismiss();
                                ToastUtil.show(ScanInputActivity.this,e.getMessage());
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

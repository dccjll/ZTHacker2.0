package com.example.auser.zthacker.ui.activity.welcome;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.ui.activity.MainActivity;
import com.example.auser.zthacker.utils.SPUtils;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivacyPolicyActivity extends BaseActivity{

    @BindView(R.id.tvPrivacyPolicy)
    TextView tvPrivacyPolicy;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.tvCancel)
    TextView tvCancel;

    public static final String TAG = "PrivacyPolicyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean ppFlag = SPUtils.getSharedBooleanData(this,"Privacy_Police_Agree_Flag");
        if (ppFlag) {
            MainActivity.startActivity(this);
            finish();
            return;
        }
        setContentView(R.layout.activity_privacy_policy);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("隐私政策");
        tvPrivacyPolicy.setText(readInfo());
    }

    private String readInfo() {
        String info = "";
        AssetManager assetManager = getAssets();
        byte[] data = null;
        try {
            InputStream is = assetManager.open("privacy_policy.info");
            Log.i(TAG, "is.available=" + is.available());
            data = new byte[is.available()];
            byte[] buffer = new byte[1024];
            int bufferReadSize;
            int destPos = 0;
            while((bufferReadSize = is.read(buffer)) != -1) {
                System.arraycopy(buffer, 0, data, destPos, bufferReadSize);
                destPos += bufferReadSize;
            }
            is.close();
            Log.i(TAG, "data.length=" + data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data != null) {
            info = new String(data);
        }
        return info;
    }

    @OnClick({R.id.image_back,R.id.tvConfirm,R.id.tvCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirm:
                SPUtils.setSharedBooleanData(this, "Privacy_Police_Agree_Flag", true);
                MainActivity.startActivity(this);
                finish();
                break;
            case R.id.image_back:
            case R.id.tvCancel:
                finish();
                break;
            default:
                break;

        }
    }
}

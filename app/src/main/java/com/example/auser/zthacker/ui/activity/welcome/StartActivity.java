package com.example.auser.zthacker.ui.activity.welcome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.utils.GetVision;
import com.example.auser.zthacker.utils.SPUtils;

/**
 * Created by zhengkq on 2017/8/23.
 */

public class StartActivity extends Activity{

    private TextView tv_versions;
    private Boolean isGuide;
    private int recLen=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        saveMetrics();
        initView();
        //检验包名和签名与各平台上面注册的是否一致
        //Review.MD5Review(this,"com.eyenorse","363a0a4d5ddcebd0ad95bc512d052482");
        //MobclickAgent.enableEncrypt(true);//友盟-如果enable为true，SDK会对日志进行加密。加密模式可以有效防止网络攻击，提高数据安全性。如果enable为false，SDK将按照非加密的方式来传输日志。
    }

    private void initView() {
        tv_versions = (TextView) findViewById(R.id.tv_versions);
        try {
            String versionName = GetVision.getAppVersionName(this);
            tv_versions.setText("版本V"+versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isGuide =  SPUtils.getSharedBooleanData(this,"isGuide");
    }

    private void saveMetrics() {
        SharedPreferences preferences = getSharedPreferences(Config.SharedPreferencesData, 0);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        preferences.edit().putInt("width", width).apply();
        preferences.edit().putInt("height", height).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSetTime();//设置倒计时
    }

    private void initSetTime() {
        Message message = handler.obtainMessage(1);     // Message
        handler.sendMessageDelayed(message, 1000);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case 1:
                    recLen--;
                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        initActivity();
                    }
            }
            super.handleMessage(msg);
        }
    };

    private void initActivity() {
        if (isGuide){
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        }else {
            GuideActivity.startActivity(this);
        }
        finish();
    }
}

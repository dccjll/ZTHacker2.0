package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.http.ApiRequestData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/11.
 */

public class FunctionIntroActivity extends BaseActivity{
    @BindView(R.id.webview)
    WebView webview;
    public static void startActivity(Context context){
        Intent intent = new Intent(context,FunctionIntroActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_intro);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("功能介绍");
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(ApiRequestData.getInstance(this).ServiceUrl);
    }

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }
}

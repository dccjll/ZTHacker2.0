package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.utils.GetVision;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/8.
 */

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.tv_versions)
    TextView tv_versions;
    public static void startActivity(Context context){
        Intent intent = new Intent(context,AboutUsActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("关于我们");

        try {
            String versionName = GetVision.getAppVersionName(this);
            tv_versions.setText("版本V"+versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.image_back,R.id.rl_intro})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rl_intro:
                FunctionIntroActivity.startActivity(this);
                break;
        }
    }
}

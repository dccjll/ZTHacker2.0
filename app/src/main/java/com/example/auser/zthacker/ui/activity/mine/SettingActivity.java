package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.dialog.LoginDialog;
import com.example.auser.zthacker.dialog.MyProgressDialog;
import com.example.auser.zthacker.utils.ClearMemoryUtils;
import com.example.auser.zthacker.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by zhengkq on 2017/8/8.
 */

public class SettingActivity extends BaseActivity{
    @BindView(R.id.iv_toggle)
    ImageView iv_toggle;
    @BindView(R.id.tv_cache)
    TextView tv_cache;

    private boolean isToggle;
    private LoginDialog dialog;
    private MyProgressDialog progressDialog;
    private int recLen = 5;
    private EventPostBean eventPostBean;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("设置");
        initMemory();
    }

    @OnClick({R.id.image_back,R.id.rl_change,R.id.rl_toggle,R.id.rl_clear_cache
    ,R.id.rl_about_us,R.id.tv_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rl_change:
               ChangePassActivity.startActivity(this);
                break;
            case R.id.rl_toggle:
                isToggle = !isToggle;
                iv_toggle.setImageDrawable(isToggle?getResources().getDrawable(R.mipmap.icon_switch_hl)
                :getResources().getDrawable(R.mipmap.icon_switch));
                break;
            case R.id.rl_clear_cache:
                ClearMemoryUtils.clearAllCache(SettingActivity.this);
                recLen = 5;
                Message message = handler.obtainMessage(1);
                handler.sendMessageDelayed(message, 1000);
                progressDialog.show();
                break;
            case R.id.rl_about_us:
                AboutUsActivity.startActivity(this);
                break;
            case R.id.tv_exit:
                SPUtils.setSharedStringData(this,"userId","");
                SPUtils.setSharedStringData(this,"pwd","");
                eventPostBean = new EventPostBean();
                eventPostBean.setType(Config.LoginOut);
                EventBus.getDefault().post(eventPostBean);
                finish();
                break;
        }
    }
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    recLen--;
                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        progressDialog.dismiss();
                        initMemory();
                    }
            }
            super.handleMessage(msg);
        }
    };

    //计算缓存
    private void initMemory() {
        progressDialog = new MyProgressDialog(SettingActivity.this, "正在清除缓存...");
        try {
            String memory = ClearMemoryUtils.getTotalCacheSize(SettingActivity.this);
            tv_cache.setText(memory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

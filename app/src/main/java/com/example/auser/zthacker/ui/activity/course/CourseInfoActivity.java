package com.example.auser.zthacker.ui.activity.course;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;
import com.example.auser.zthacker.utils.TextUtil;
import java.io.Serializable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/9/4.
 */

public class CourseInfoActivity extends BaseActivity{
    @BindView(R.id.rl_topview)
    RelativeLayout rl_topview;
    @BindView(R.id.centre_text)
    TextView centre_text;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.ll_top_bottom_line)
    LinearLayout ll_top_bottom_line;
    @BindView(R.id.webview)
    WebView webview;
    private Intent intent;
    private HomeClassTypeInfoBean courseInfoBean;

    public static void startActivity(Context context,HomeClassTypeInfoBean courseInfoBean) {
        Intent intent = new Intent(context, CourseInfoActivity.class);
        intent.putExtra("courseInfoBean", (Serializable) courseInfoBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.course_info_top_bg);
        centre_text.setTextColor(getResources().getColor(R.color.white));
        setCentreText("课程详情");
        rl_topview.setBackgroundColor(getResources().getColor(R.color.course_info_top_bg));
        iv_back.setImageResource(R.mipmap.top_icon_back_white);
        ll_top_bottom_line.setVisibility(View.GONE);
        intent = getIntent();
        courseInfoBean = (HomeClassTypeInfoBean) intent.getSerializableExtra("courseInfoBean");
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error);
            }
        });
        //加载需要显示的网页
        webview.loadUrl(courseInfoBean.getAddLink());
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!TextUtil.isNull(url)){
                    CourseInfoIntroActivity.startActivity(CourseInfoActivity.this,url,courseInfoBean);
                    Log.e("dadadada",url);
                }
                return true;
            }
        });
    }

    @OnClick({R.id.image_back, R.id.iv_contact_line})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.iv_contact_line:
                ClassConsultActivity.startActivity(this, Config.CLASS_CONSULT);
                break;
        }
    }
}

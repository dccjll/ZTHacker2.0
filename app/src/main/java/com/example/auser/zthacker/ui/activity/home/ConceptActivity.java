package com.example.auser.zthacker.ui.activity.home;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.ui.activity.mine.ClassConsultActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/22.
 */

public class ConceptActivity extends BaseActivity {
    @BindView(R.id.rl_topview)
    RelativeLayout rl_topview;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.iv_contact_line)
    ImageView iv_contact_line;
    private Intent intent;
    private String url;
    private String title;
    private int type;

    /*type(webview打开文件的类型，1代表普通的h5页面，2代表用webview打开ppt)
      ppt预览使用微软提供的预览效果，使用WebView打开
      将文件地址和http://view.officeapps.live.com/op/view.aspx?src=
      拼接成新的url，再用webview加载即可。*/
    public static void startActivity(Context context,String url,String title,int type) {
        Intent intent = new Intent(context, ConceptActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title",title);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concept);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        rl_topview.setBackgroundColor(getResources().getColor(R.color.white));
        intent = getIntent();
        type = intent.getIntExtra("type", 0);
        url = intent.getStringExtra("url");
        if (type==2){
           url = "http://view.officeapps.live.com/op/view.aspx?src="+url;
            iv_contact_line.setVisibility(View.GONE);

        }
        title = intent.getStringExtra("title");
        setCentreText(title);
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
        webview.loadUrl(url);
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

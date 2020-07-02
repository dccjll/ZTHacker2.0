package com.example.auser.zthacker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.auser.zthacker.R;

/**
 * Created by zhengkq on 2018-1-19.
 */

public class ShareDialog extends Dialog implements View.OnClickListener{
    private ShareDialog.OneShareListener onClickChoose = null;

    public ShareDialog(Context context) {
        super(context, R.style.HeadSelectDialog);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_share);
        findViewById(R.id.wechat).setOnClickListener(this);
        findViewById(R.id.WechatMoments).setOnClickListener(this);
        findViewById(R.id.sina).setOnClickListener(this);
        findViewById(R.id.qq).setOnClickListener(this);
        findViewById(R.id.textView_cancel).setOnClickListener(this);
    }

    public static abstract class OneShareListener {
        //微信朋友圈
        public abstract void onWXPYQ();

        //微信好友
        public abstract void onWXHY();

        //QQ
        public abstract void onQQ();

        //新浪
        public abstract void onSina();

    }

    public void setOnClickChoose(ShareDialog.OneShareListener onClickChoose){
        this.onClickChoose = onClickChoose;
    }
    public interface OnClickChoose {
        public void onClick(int id);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.wechat:
                if (onClickChoose != null) {
                    onClickChoose.onWXHY();
                }
                dismiss();
                break;
            case R.id.WechatMoments:
                if (onClickChoose != null) {
                    onClickChoose.onWXPYQ();
                }
                dismiss();
                break;
            case R.id.sina:
                if (onClickChoose != null) {
                    onClickChoose.onSina();
                }
                dismiss();
                break;
            case R.id.qq:
                if (onClickChoose != null) {
                    onClickChoose.onQQ();
                }
                dismiss();
                break;
            case R.id.textView_cancel:
                dismiss();
                break;
        }
    }
}

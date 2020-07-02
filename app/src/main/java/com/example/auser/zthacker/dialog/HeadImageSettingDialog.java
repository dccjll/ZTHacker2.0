package com.example.auser.zthacker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.auser.zthacker.R;

/**
 * Created by zhengkq on 2017/1/7.
 */

public class HeadImageSettingDialog extends Dialog implements View.OnClickListener {
    private HeadImageSettingDialog.OnClickChoose onClickChoose = null;

    public HeadImageSettingDialog(Context context) {
        super(context, R.style.HeadSelectDialog);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_head_setting);
        findViewById(R.id.textView_cancel).setOnClickListener(this);
        findViewById(R.id.textView_local).setOnClickListener(this);
        findViewById(R.id.textView_camera).setOnClickListener(this);
    }

    public void setOnClickChoose(HeadImageSettingDialog.OnClickChoose onClickChoose){
        this.onClickChoose = onClickChoose;
    }
    public interface OnClickChoose {
        public void onClick(int id);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.textView_cancel:
                onClickChoose.onClick(3);
                dismiss();
                break;
            case R.id.textView_local:
                onClickChoose.onClick(2);
                dismiss();
                break;
            case R.id.textView_camera:
                onClickChoose.onClick(1);
                dismiss();
                break;
        }
    }
}

package com.example.auser.zthacker.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.utils.TextUtil;

/**
 * Created by zhengkq on 2017/8/16.
 */

public class MyPublishDelDialog extends Dialog implements View.OnClickListener {
    private OnClickChoose onClickChoose = null;
    private String del;
    private String report;
    private TextView textView_del;
    private TextView textView_report;
    private ImageView iv_divider;

    public MyPublishDelDialog(@NonNull Context context, String del, String report) {
        super(context, R.style.HeadSelectDialog);
        this.del = del;
        this.report = report;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_publish_del);
        findViewById(R.id.textView_cancel).setOnClickListener(this);
        textView_del = (TextView) findViewById(R.id.textView_del);
        textView_report = (TextView) findViewById(R.id.textView_report);
        iv_divider = (ImageView) findViewById(R.id.iv_divider);
        textView_report.setText(del);
        textView_report.setOnClickListener(this);
        if (TextUtil.isNull(report)) {
            textView_del.setVisibility(View.GONE);
            iv_divider.setVisibility(View.GONE);
        } else {
            textView_del.setVisibility(View.VISIBLE);
            iv_divider.setVisibility(View.VISIBLE);
            textView_del.setOnClickListener(this);
        }
    }

    public void setOnClickChoose(OnClickChoose onClickChoose) {
        this.onClickChoose = onClickChoose;
    }

    public interface OnClickChoose {
        public void onClick(int id);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.textView_cancel:
                onClickChoose.onClick(3);
                dismiss();
                break;
            case R.id.textView_del:
                onClickChoose.onClick(2);
                dismiss();
                break;
            case R.id.textView_report:
                onClickChoose.onClick(1);
                dismiss();
        }
    }
}

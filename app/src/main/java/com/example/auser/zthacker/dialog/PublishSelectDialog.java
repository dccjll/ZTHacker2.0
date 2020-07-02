package com.example.auser.zthacker.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.ui.activity.found.PublishTextActivity;
import com.example.auser.zthacker.ui.fragment.main.main.FoundFragment;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/9.
 */

public class PublishSelectDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String firstText;
    private String secondText;
    public TextView tv_first_text;
    public TextView tv_second_text;
    private PublishSelectDialog.OnClickChoose onClickChoose = null;

    public PublishSelectDialog(@NonNull Context context, String firstText, String secondText) {
        super(context, R.style.HeadSelectDialog);
        this.context =  context;
        this.firstText = firstText;
        this.secondText = secondText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_publish_select);
        tv_first_text = (TextView) findViewById(R.id.tv_first_text);
        tv_second_text = (TextView) findViewById(R.id.tv_second_text);
        tv_first_text.setText(firstText);
        tv_second_text.setText(secondText);
        tv_first_text.setOnClickListener(this);
        tv_second_text.setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_first_text:
                onClickChoose.onClick(0);
                dismiss();
                break;
            case R.id.tv_second_text:
                onClickChoose.onClick(1);
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    public void setOnClickChoose(PublishSelectDialog.OnClickChoose onClickChoose){
        this.onClickChoose = onClickChoose;
    }
    public interface OnClickChoose {
        public void onClick(int id);
    }
}

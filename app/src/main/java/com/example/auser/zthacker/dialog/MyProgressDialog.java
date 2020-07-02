package com.example.auser.zthacker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.auser.zthacker.R;

/**
 * Created by zhengkq on 2016/12/30.
 */
public class MyProgressDialog extends Dialog {
    String hint = "";

public MyProgressDialog(Context context, String hint) {
        super(context, R.style.TransparentDialog);
        this.hint = hint;
        }



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_my_progress);

        ((TextView)findViewById(R.id.textVIew_my_progress_hint)).setText(hint);
        findViewById(R.id.linearLayout_my_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

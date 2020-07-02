package com.example.auser.zthacker.dialog;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.app.AppApplication;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.ui.fragment.main.main.FoundFragment;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;


public class LoadingDialog {
    /**
     * 加载数据对话框
     */
    private static Dialog mLoadingDialog;
    private static LoginDialog dialog;
    private static MyPublishDelDialog myPublishDelDialog;
    private static PublishSelectDialog myPublishDialog;

    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param msg        对话框显示内容
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(Activity context, String msg, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView loadingText = (TextView) view.findViewById(R.id.id_tv_loading_dialog_text);
        loadingText.setText(msg);

        if (mLoadingDialog == null) {
            mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
            mLoadingDialog.setCancelable(cancelable);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        mLoadingDialog.show();
        return mLoadingDialog;
    }

    public static Dialog showDialogForLoading(Activity context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView loadingText = (TextView) view.findViewById(R.id.id_tv_loading_dialog_text);
        loadingText.setText("加载中...");
        loadingText.setTextColor(context.getResources().getColor(R.color.text_64));
        System.out.println("开启进度条");
        if (mLoadingDialog == null) {
            mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
            mLoadingDialog.setCancelable(true);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        mLoadingDialog.show();
        return mLoadingDialog;
    }

    public static Dialog showDialogForLogin(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
        dialog = new LoginDialog(context);
        dialog.setView(view);
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM;
        dialog.setCanceledOnTouchOutside(false);
        Log.e("dafdgsgs", "-----------");
        dialog.show();
        return dialog;
    }

    public static Dialog showDialogForDelAndReport(Context context, String del,String report) {
        myPublishDelDialog = new MyPublishDelDialog(context, del,report);
        myPublishDelDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams attributes = myPublishDelDialog.getWindow().getAttributes();
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM;
        myPublishDelDialog.show();
        return myPublishDelDialog;
    }

    public static Dialog showDialogForPublish(Context context, String firstText, String secondText) {
        myPublishDialog = new PublishSelectDialog(context, firstText, secondText);
        WindowManager.LayoutParams attributes = myPublishDialog.getWindow().getAttributes();
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM;
        myPublishDialog.setCanceledOnTouchOutside(true);
        myPublishDialog.show();
        return myPublishDialog;
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            System.out.println("关闭进度条");
        }
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLogin() {
        if (dialog != null) {
            dialog.cancel();
        }
    }
}

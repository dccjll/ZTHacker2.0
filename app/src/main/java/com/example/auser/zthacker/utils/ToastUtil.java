package com.example.auser.zthacker.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/2/26.
 */

public class ToastUtil {
    static Toast mToast;

    /***
     * 单例Toast
     */
    public static void show(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
    public static void showHttpError(Context mContext) {
        show(mContext,"请求异常，请稍后");
    }

}

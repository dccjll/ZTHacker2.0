package com.example.auser.zthacker.http;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/9/29.
 */
public class CommonUtils {

    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static Handler handler = new Handler(Looper.getMainLooper());

    //在子线程中执行任务
    public static void runInThread(Runnable task) {
        threadPool.execute(task);
    }


    public Handler getHandler() {
        return handler;
    }

    //在主线程执行任务
    public static void runOnUIThread(Runnable task) {
        handler.post(task);
    }

    //在主线程里吐司
    private static Toast toast;

    public static void showToast(final Context context, final String text) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                }
                toast.setText(text);
                toast.show();
            }
        });
    }

    /**
     * 获取应用程序版本名
     */

    public static String getAppVersionName(Context context){
        return getPackageInfo(context).versionName;
    }

    public static PackageInfo getPackageInfo(Context context){
        PackageInfo pi = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            pi = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    /**
     * 返回终端型号
     */
    public static String getModel(){
        String model=android.os.Build.MODEL; // 手机型号
        return model;
    }

    //返回移动终端的软件版本

    public static String getSystemVersion(){
        String release=android.os.Build.VERSION.RELEASE; // android系统版本号
        return release;
    }


    /**
     * 获取终端唯一ID
     * @param context
     * @return
     */
    public static String getUDID(Context context) {
        String udid = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(udid) || udid.equals("9774d56d682e549c")
                || udid.length() < 15) {
            SecureRandom random = new SecureRandom();
            udid = new BigInteger(64, random).toString(16);
        }

        if ( TextUtils.isEmpty(udid) ) {
            udid = "";
        }

        return udid;
    }


    public static void showDialog(Context context , String conttent, String positiveMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(conttent);

        builder.setPositiveButton(positiveMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

}

package com.example.auser.zthacker.http;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.KeyEvent;

import com.example.auser.zthacker.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by zoubo on 2016/3/2.
 */
public class AsyncHttpUtils {
    private Gson gson;
    private AsyncHttpClient client = null;
    private Context context;
    private ProgressDialog pDialog;//数据加载显示
    private boolean isDdismiss = true;
    private static AsyncHttpUtils ahu = null;
    private boolean isShowing;

    private AsyncHttpUtils() {
        client = new AsyncHttpClient();
        client.setTimeout(30000);//设置超时请求时间
        gson = new Gson();
    }

    public static AsyncHttpUtils getInstance(Context context) {
        if (ahu == null) {
            synchronized (AsyncHttpUtils.class) {
                AsyncHttpUtils sPf = ahu;
                if (sPf == null) {
                    sPf = new AsyncHttpUtils();
                    ahu = sPf;
                }
            }

        }
        ahu.context = context;
        return ahu;
    }

    /**
     * POST请求数据方式
     */
    public void doPost(final String url, final RequestParams params,
                       final IOAuthReturnCallBack iOAuthReturnCallBack, final Boolean... bol) {
        if (!isConnecting(context)) {
            FactoryTools.setToastShow(context.getApplicationContext(), context
                    .getResources().getString(R.string.exception));
            getDialogDismiss();
            return;
        }
        if (bol != null) {
            Log.e("httpurl", url + "参数：" + params.toString());
        }
        client.post(context, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                getDialogDismiss();
                Log.e("httperror", "错误原因post：" + arg3.getMessage() + "——请求URL：" + url + "——请求参数：" + params.toString());
                if (iOAuthReturnCallBack != null) {
                    CommonUtils.showToast(context, context.getString(R.string.exceptions));  //加上了异常信息
                    iOAuthReturnCallBack.onFailure("", arg3.getMessage() + "");
                }
                getHttpCode(arg0);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                // try{
                getDialogDismiss();
                if (arg0 == 200) {
                    if (iOAuthReturnCallBack != null) {
                        String result = new String(arg2);
                        /*if (bol != null) {
                            Log.e("httpresult", result);
                            try {
                                String error = new JSONObject(result).getString("error");
                                if (!TextUtil.isNull(error) || error.length() > 0) {
                                    ErrorInfo errorInfo = gson.fromJson(result, ErrorInfo.class);
                                    if (TextUtil.isNull(errorInfo.getStatus())) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (dialog==null||!dialog.isShowing()){
                                            initOutLineDialog(context, errorInfo);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }*/
                        iOAuthReturnCallBack.onSuccess(result, gson);
                    }
                }
//                } catch (Exception e) {
//                    FactoryTools.setToastShow(context.getApplicationContext(),
//                            context.getResources().getString(R.string.exceptions));
//                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int percentageSize = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (iOAuthReturnCallBack != null) {
                    iOAuthReturnCallBack.onProgress(bytesWritten, totalSize, percentageSize);
                }
            }

        });
    }

   /* private void initOutLineDialog(final Context context, ErrorInfo errorInfo) {
        String time = errorInfo.getLogintime();
        dialog = new OutLineNoticeDialog(context, errorInfo);
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnClickChoose(new OutLineNoticeDialog.OnClickChoose() {
            @Override
            public void onClick(boolean f) {
                if (f) {
                    SharedPreferences sp = context.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isload", false);
                    editor.putInt("memberid", 0);
                    editor.putString("token", "");
                    editor.commit();
                    LoginActivity.startActivity(context);
                    ((Activity) context).finish();
                }
            }
        });
        dialog.show();
    }*/

    /**
     * POST请求数据方式
     */
    public void doPost(final String url, final HttpEntity entity,
                       final IOAuthReturnCallBack iOAuthReturnCallBack, final Boolean... bol) {
        if (!isConnecting(context)) {
            FactoryTools.setToastShow(context.getApplicationContext(), context
                    .getResources().getString(R.string.exception));
            getDialogDismiss();
            return;
        }
        if (bol != null) {
            Log.e("httpurl", url + "参数：" + entity.toString());
        }
        client.post(context, url, entity, "text/xml", new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                getDialogDismiss();
                Log.e("httperror", "错误原因post：" + arg3.getMessage() + "——请求URL：" + url + "——请求参数：" + entity.toString());
                if (iOAuthReturnCallBack != null) {
                    iOAuthReturnCallBack.onFailure("", arg3.getMessage() + "");
                }
                getHttpCode(arg0);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                // try{
                getDialogDismiss();
                if (arg0 == 200) {
                    if (iOAuthReturnCallBack != null) {
                        String result = new String(arg2);
                       /* if (bol != null) {
                            Log.e("httpresult", result);
                            try {
                                String error = new JSONObject(result).getString("error");
                                if (!TextUtil.isNull(error) || error.length() > 0) {
                                    ErrorInfo errorInfo = gson.fromJson(result, ErrorInfo.class);
                                    if (TextUtil.isNull(errorInfo.getStatus())) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (dialog==null||!dialog.isShowing()){
                                            initOutLineDialog(context, errorInfo);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/
                        iOAuthReturnCallBack.onSuccess(result, gson);
                    }
                }
//                } catch (Exception e) {
//                    FactoryTools.setToastShow(context.getApplicationContext(),
//                            context.getResources().getString(R.string.exceptions));
//                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int percentageSize = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (iOAuthReturnCallBack != null) {
                    iOAuthReturnCallBack.onProgress(bytesWritten, totalSize, percentageSize);
                }
            }

        });
    }

    /**
     * GET请求数据方式
     */
    public void doGet(String url, Map<String, String> params,
                      final IOAuthReturnCallBack iOAuthReturnCallBack, final Boolean... bol) {
        if (!isConnecting(context)) {
            FactoryTools.setToastShow(context.getApplicationContext(), context
                    .getResources().getString(R.string.exception));
            getDialogDismiss();
            return;
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(url);
        if (params != null) {
            sb.append("?");
            for (Map.Entry<String, String> par : params.entrySet()) {
                sb.append(par.getKey()).append("=")
                        .append(par.getValue().toString()).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);// 去除最后一个&符号
        }
        if (bol != null) {
            Log.e("httpurl", sb.toString());
        }
        client.get(context, sb.toString(), new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                getDialogDismiss();
                Log.e("httperror", "错误原因get：" + arg3.getMessage() + "——请求URL：" + sb.toString());
                if (iOAuthReturnCallBack != null) {
                    iOAuthReturnCallBack.onFailure("", arg3.getMessage() + "");
                }
                getHttpCode(arg0);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                // try {
                getDialogDismiss();
                String result = new String(arg2);
               /* if (arg0 == 200) {
                    if (iOAuthReturnCallBack != null) {
                        String result = new String(arg2);
                        *//*if (bol != null) {
                            Log.e("httpresult", result);
                            try {
                                String error = new JSONObject(result).getString("error");
                                if (!TextUtil.isNull(error) || error.length() > 0) {
                                    ErrorInfo errorInfo = gson.fromJson(result, ErrorInfo.class);
                                    if (TextUtil.isNull(errorInfo.getStatus())) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (dialog==null||!dialog.isShowing()){
                                            initOutLineDialog(context, errorInfo);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }*//*
                        iOAuthReturnCallBack.onSuccess(result, gson);
                    }
                }*/
//                } catch (Exception e) {
//                    FactoryTools.setToastShow(context.getApplicationContext(),
//                            context.getResources().getString(R.string.exceptions));
//                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int percentageSize = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (iOAuthReturnCallBack != null) {
                    iOAuthReturnCallBack.onProgress(bytesWritten, totalSize, percentageSize);
                }
            }
        });
    }

    /**
     * 文件下载
     */
    public void doFlie(final String url, File file,
                       final FileAuthReturnCallBack fileAuthReturnCallBack, final Boolean... bol) {
        if (!isConnecting(context)) {
            FactoryTools.setToastShow(context, context
                    .getResources().getString(R.string.exception));
            getDialogDismiss();
            return;
        }
        client.get(url, new FileAsyncHttpResponseHandler(file) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                getDialogDismiss();
                Log.e("httperror", "错误原因：" + throwable.getMessage() + "——请求URL：" + url.toString());
                if (fileAuthReturnCallBack != null) {
                    fileAuthReturnCallBack.onFailure("", throwable.getMessage() + "");
                }
                getHttpCode(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                getDialogDismiss();
                if (statusCode == 200 && fileAuthReturnCallBack != null) {
                    fileAuthReturnCallBack.onSuccess(file);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int percentageSize = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (fileAuthReturnCallBack != null) {
                    fileAuthReturnCallBack.onProgress(bytesWritten, totalSize, percentageSize);
                }
                if (pDialog != null && pDialog.isShowing()) {
                    // 下载进度显示
                    pDialog.setProgress(percentageSize);
                }
            }
        });
    }


    /**
     * 数据加载框
     *
     * @param message
     * @return
     */
    public Dialog ShowDialog(String message) {
        // FactoryTools.setToastShow(context,(((Activity) context).isFinishing())+"");
        if (((Activity) context).isFinishing()) {
            return null;
        }
        if (message == null) {
            message = "数据加载中...";
        }

        getDialogDismiss();
        pDialog = new ProgressDialog(context);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage(message);
        pDialog.show();
        pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && keyEvent.getRepeatCount() == 0) {
                    cancel();
                    pDialog = null;
                }
                return false;
            }
        });
        isDdismiss = true;
        return pDialog;
    }

    /**
     * 数据加载框
     *
     * @param message
     * @param isDdismiss
     * @return
     */
    public Dialog ShowDialog(String message, boolean isDdismiss) {
        Dialog dialog = ShowDialog(message);
        this.isDdismiss = isDdismiss;
        return dialog;
    }

    /**
     * 是否自动关闭数据加载对话框
     *
     * @param isDdismiss
     */
    public void setDdismiss(boolean isDdismiss) {
        this.isDdismiss = isDdismiss;
    }

    /**
     * 下载进度显示
     *
     * @param message
     * @return
     */
    public Dialog ShowFileDialog(String message) {
        if (((Activity) context).isFinishing()) {
            return null;
        }
        if (message == null) {
            message = "正在下载，请稍等...";
        }

        getDialogDismiss();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle(context.getString(R.string.app_name));
        pDialog.setMessage(message);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取   消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                cancel();
                pDialog = null;
            }
        });
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        return pDialog;
    }

    /**
     * 关闭提示框
     *
     * @return
     */
    public Dialog getDialogDismiss() {
        if (pDialog != null && pDialog.isShowing() && isDdismiss) {
            pDialog.dismiss();
            //cancel();
            pDialog = null;
        }
        return pDialog;
    }


    /*网络工具方法*/

    /**
     * 服务请求失败提示
     *
     * @param statusCode
     */
    private void getHttpCode(int statusCode) {
        Log.e("statusCode", statusCode + "");
        switch (statusCode) {
            case 408:
                FactoryTools.setToastShow(context.getApplicationContext(),
                        "请求服务超时！");
                break;
            default:
                FactoryTools.setToastShow(context.getApplicationContext(),
                        context.getResources().getString(R.string.exceptions));
                break;
        }

    }

    /**
     * 是否连接网络
     */
    private boolean isConnecting(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo in : info) {
                if (in.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 取消当前网络请求
     */
    public void cancel() {
        if (client != null) {
            //client.cancelAllRequests(true);
            client.cancelRequests(context, true);
        }
    }
}

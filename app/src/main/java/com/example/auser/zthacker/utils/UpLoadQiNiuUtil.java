package com.example.auser.zthacker.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/5/4.
 */
public class UpLoadQiNiuUtil {
    private final static int PROGERSS = 1;
    private final static int UPLOAD_SUCCEED = 2;
    public static void uploadFile(final Context context, final File file, final String token, final String expectKey, final boolean isImg, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UploadManager uploadManager = new UploadManager();
                final UpProgressHandler progressHandler = new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        Message msg = new Message();
                        msg.what = PROGERSS;
                        msg.arg1 = (int) (percent * 100);
                        handler.sendMessage(msg);
                    }
                };
                Map<String, String> params = new HashMap<>();
                params.put("x:foo", "fooval");//自定义变量，key必须以 x: 开始。
                final UploadOptions opt = new UploadOptions(params, null, true, progressHandler, null);
                UUID uuid = UUID.randomUUID();
                final String uuidStr;
                uuidStr = uuid.toString().replace("-", "") + ".mp4";
                String key =   uuidStr;
                uploadManager.put(file, key, token, new UpCompletionHandler() {

                    @Override
                    public void complete(String key, com.qiniu.android.http.ResponseInfo info, JSONObject response) {
                        // TODO Auto-generated method stub
                        Message msg = new Message();
                        msg.what = UPLOAD_SUCCEED;
                        if (info.isOK()) {
                            Log.e("上传_七牛", "上传成功了");
                            String filePath = expectKey + "/" + key;
                            msg.obj = filePath;
                            if (isImg) {
                                msg.arg1 = 1;
                            } else {
                                msg.arg1 = 2;
                            }
                            handler.sendMessage(msg);
                            Log.e("路径", filePath);
                            Toast.makeText(context, "上传成功了", Toast.LENGTH_LONG);
                        } else {
                            Log.e("上传失败", "上传失败");
                        }
                        Date date = new Date();
                        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
                        String LgTime = sdformat.format(date);
                        Log.e("timeFinish", LgTime);
                    }
                }, opt);
            }
        }).start();
    }

    public static void getUpimg(final Context context, final String path, final String upToken, final String expectKey, final Handler handler) {
        // 图片上传到七牛 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        UploadManager uploadManager = new UploadManager();
        UUID uuid = UUID.randomUUID();
        final String uuidStr;
        uuidStr = uuid.toString().replace("-", "") + ".jpg";
        String key = uuidStr;
        uploadManager.put(path, key, upToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info,
                                         JSONObject res) {
                        // res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n "
                                + res);
                        //Log.e("error111", info.error);
                        Message msg = new Message();
                        msg.what = UPLOAD_SUCCEED;
                        try {
                            // 七牛返回的文件名
                            String upimg = res.getString("key");
                            if (info.isOK()) {
                                String imgUrl = expectKey + "/" + upimg;
                                Toast.makeText(context, "上传成功了", Toast.LENGTH_LONG);
                                msg.obj = imgUrl;
                                msg.arg1 = 1;//1为图片 2为视频
                                handler.sendMessage(msg);
                            } else {
                                Log.e("上传失败", "上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
    }


    public static abstract class ProgressAndisCompleteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGERSS:
                    progress(msg.arg1);
                    break;
                case UPLOAD_SUCCEED:
                    String filePath = (String) msg.obj;
                    int type = msg.arg1;
                    isComplete(filePath, type);
                    break;
                default:
                    break;
            }

        }

        /**
         * 上传进度的回调
         *
         * @param progress 上传进度百分比回调
         */
        public abstract void progress(int progress);

        /**
         * 上传成功的回调
         *
         * @param videoPath
         */
        public abstract void isComplete(String videoPath, int type);
    }
}

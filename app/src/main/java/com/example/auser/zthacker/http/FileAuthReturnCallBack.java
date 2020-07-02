package com.example.auser.zthacker.http;

import java.io.File;

/**
 * Created by zoubo on 2016/3/2.
 */
public abstract class FileAuthReturnCallBack {
     public abstract void onSuccess(File file);
     public void onFailure(String arg0, String arg1){}
     public void onProgress(long bytesWritten, long totalSize,long percentageSize){}//下载进度
}


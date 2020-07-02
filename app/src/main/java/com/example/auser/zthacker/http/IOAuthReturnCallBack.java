package com.example.auser.zthacker.http;

import com.google.gson.Gson;

/**
 * Created by zoubo on 2016/3/2.
 */
public abstract class IOAuthReturnCallBack{
    public abstract void onSuccess(String result, Gson gson);
    public void onFailure(String arg0, String arg1){}
    public void onProgress(long bytesWritten, long totalSize,long percentageSize){}//数据加载进度进度
}


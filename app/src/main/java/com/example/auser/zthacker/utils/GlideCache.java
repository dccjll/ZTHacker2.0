package com.example.auser.zthacker.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by zhengkq on 2018-1-18.
 */

public class GlideCache implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        //设置图片的显示格式ARGB_8888(指图片大小为32bit)
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        //设置磁盘缓存目录（和创建的缓存目录相同）
       /* if (FolderManager.isSdCardExist()){
            File storageDirectory = Environment.getExternalStorageDirectory();
            String downloadDirectoryPath=storageDirectory+"/glideCache";
            File file=new File(downloadDirectoryPath);
            if(!file.exists()) {
                file.mkdir();
                Log.e("dddddcache",file.getAbsolutePath());
            }
            Log.e("dddddcache",file.getAbsolutePath()+"----");
            // 设置磁盘缓存为20M，缓存在内部缓存目录
            int cacheSizeMegaBytes = 20*1024*1024;
            builder.setDiskCache(
                    new InternalCacheDiskCacheFactory(context,downloadDirectoryPath, cacheSizeMegaBytes)
            );
            //设置缓存的大小为60M
            int cacheSize = 80*1024*1024;
            builder.setDiskCache(new DiskLruCacheFactory(downloadDirectoryPath, cacheSize));
            Log.e("dddddcache","------------------1");
        }else {*/
            // 设置内存缓存为20M，缓存在内部缓存目录
            int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
            builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
            //设置Bitmap池的大小为30M
            int bitmapPoolSizeBytes = 1024 * 1024 * 30; // 30mb
            builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));
            //磁盘缓存
            int diskCacheSizeBytes = 1024*1024*100;  //100MB
            builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
        //}
       /* builder.setBitmapPool();
        builder.setArrayPool();*/
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
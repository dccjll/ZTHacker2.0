/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.auser.zthacker.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.example.auser.zthacker.app.AppApplication;
import com.example.auser.zthacker.bean.UtilBean;

import java.util.HashMap;



/**
 * @author 咖枯
 * @version 1.0 2016/5/31
 */
public class MyUtils {
    private static String duration;


    //private static MediaMetadataRetriever mmr;

    public static void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabWidth = calculateTabWidth(tabLayout);
        int screenWidth = getScreenWith();

        if (tabWidth <= screenWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    private static int calculateTabWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0); // 通知父view测量，以便于能够保证获取到宽高
            tabWidth += view.getMeasuredWidth();
        }
        return tabWidth;
    }

    public static int getScreenWith() {
        return AppApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static UtilBean getVideoThumb(String path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(path, new HashMap<String, String>());
            } else {
                retriever.setDataSource(path);
            }
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_NEXT_SYNC);
            // 播放时长单位为毫秒
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, getScreenWith(), getScreenWith()*2/3,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        UtilBean utilBean = new UtilBean();
        utilBean.setBitmap(bitmap);
        utilBean.setaString(duration);
        utilBean.setUpToken(path);
        //releaseBitmap(bitmap);
        return utilBean;
        /*//创建FFmpegMediaMetadataRetriever对象
        Bitmap bitmap = null;
        FFmpegMediaMetadataRetriever mm=new FFmpegMediaMetadataRetriever();

        try{
            //获取视频文件数据
            mm.setDataSource(path);
            //获取文件缩略图
            bitmap=mm.getFrameAtTime();
        }catch (Exception e){
        }
        finally {
            mm.release();
        }
        return bitmap;*/
    }

    public static void releaseBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static String getVideoTime(String path) {
        String duration = null;
        MediaMetadataRetriever  mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path, new HashMap<String, String>());
            // 播放时长单位为毫秒
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return duration;
    }

    //转换为%E4%BD%A0形式
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
}

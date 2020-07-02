package com.example.auser.zthacker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.auser.zthacker.utils.localImages.GetRoundBitmap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhengkq on 2017-11-9.
 */

public class ImagePathToFile {
    public static  Bitmap ImagePathToByte(String realFilePath){
        //采样压缩
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(realFilePath, options2);
        return bitmap;
    }

    public static File operaFileData(String path, byte[] by) {
        {
            FileOutputStream fileout = null;
            String fileName = path;
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                fileout = new FileOutputStream(file);
                fileout.write(by, 0, by.length);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    fileout.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return file;
        }
    }


    public static ByteArrayInputStream getByteArrayInputStream(File file){
        return new ByteArrayInputStream(getByetsFromFile(file));
    }
    /**
     *  ByteArrayInputStream ins = new ByteArrayInputStream(picBytes);
     * @param file
     * @return
     */
    public static byte[] getByetsFromFile(File file){
        FileInputStream is = null;
        // 获取文件大小
        long length = file.length();
        // 创建一个数据来保存文件数据
        byte[] fileData = new byte[(int)length];

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int bytesRead=0;
        // 读取数据到byte数组中
        while(bytesRead != fileData.length) {
            try {
                bytesRead += is.read(fileData, bytesRead, fileData.length - bytesRead);
                if(is != null)
                    is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return fileData;
    }
}

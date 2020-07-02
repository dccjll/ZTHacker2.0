package com.example.auser.zthacker.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/11/7.
 */

public class UtilBean implements Serializable{
    private Bitmap bitmap;
    private String aString;
    private String upToken;
    private int state;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public String getUpToken() {
        return upToken;
    }

    public void setUpToken(String upToken) {
        this.upToken = upToken;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

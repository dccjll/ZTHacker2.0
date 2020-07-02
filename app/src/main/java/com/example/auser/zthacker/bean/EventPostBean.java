package com.example.auser.zthacker.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/30.
 */

public class EventPostBean implements Serializable{
    private String type;
    private String message;
    private String id;
    private int count;
    private Bitmap bitmap;
    private PublishInfoList.PublishInfo publishInfo;
    private NewsInfoBean newsInfoBean;

    public PublishInfoList.PublishInfo getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(PublishInfoList.PublishInfo publishInfo) {
        this.publishInfo = publishInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NewsInfoBean getNewsInfoBean() {
        return newsInfoBean;
    }

    public void setNewsInfoBean(NewsInfoBean newsInfoBean) {
        this.newsInfoBean = newsInfoBean;
    }
}

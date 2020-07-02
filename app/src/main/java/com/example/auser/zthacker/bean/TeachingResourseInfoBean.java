package com.example.auser.zthacker.bean;

import com.example.auser.zthacker.utils.TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-25.
 */

public class TeachingResourseInfoBean implements Serializable {
    /* "isNewRecord": true,
               "fileName": "空中霹雳-回旋飞机",
               "videoSynopsis": "了解特技飞行表演；学习尾翼和副翼的科学原理；探究襟翼对回旋飞机飞行轨迹的影响。",
               "courseWareName": "空中霹雳-回旋飞机",
               "fileUrl": "http://oxy5a0fop.bkt.clouddn.com/lifpWt8igGxtqvAX5j-NbO9pLjpC",
               "fileUrlImg": "http://p3gq0kfyp.bkt.clouddn.com/img_pilot_videocover_3.jpg",
               "courseWareImgUrl": "http://p3gq0kfyp.bkt.clouddn.com/%E5%B0%81-003%20%E7%A9%BA%E4%B8%AD%E9%9C%B9%E9%9B%B3-%E2%80%9CRAMBIRD%E2%80%9D%E5%9B%9E%E6%97%8B%E9%A3%9E%E6%9C%BA.png",
               "courseWarePPTUrl": "http://p3gq0kfyp.bkt.clouddn.com/%E7%9F%AD-003%20%E7%A9%BA%E4%B8%AD%E9%9C%B9%E9%9B%B3-%E2%80%9CRAMBIRD%E2%80%9D%E5%9B%9E%E6%97%8B%E9%A3%9E%E6%9C%BA.pptx",
               "courseWarePDFUrl": "http://p3gq0kfyp.bkt.clouddn.com/%E7%9F%AD-003%20%E7%A9%BA%E4%B8%AD%E9%9C%B9%E9%9B%B3-%E2%80%9CRAMBIRD%E2%80%9D%E5%9B%9E%E6%97%8B%E9%A3%9E%E6%9C%BA.pdf"*/
    private boolean isNewRecord;
    private String fileName;
    private String videoSynopsis;
    private String courseWareName;
    private String fileUrl;
    private String fileUrlImg;
    private String courseWareImgUrl;
    private String courseWarePPTUrl;
    private String courseWarePDFUrl;

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVideoSynopsis() {
        return videoSynopsis;
    }

    public void setVideoSynopsis(String videoSynopsis) {
        this.videoSynopsis = videoSynopsis;
    }

    public String getCourseWareName() {
        return courseWareName;
    }

    public void setCourseWareName(String courseWareName) {
        this.courseWareName = courseWareName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrlImg() {
        return fileUrlImg;
    }

    public void setFileUrlImg(String fileUrlImg) {
        this.fileUrlImg = fileUrlImg;
    }

    public String getCourseWareImgUrl() {
        return courseWareImgUrl;
    }

    public void setCourseWareImgUrl(String courseWareImgUrl) {
        this.courseWareImgUrl = courseWareImgUrl;
    }

    public String getCourseWarePPTUrl() {
        return courseWarePPTUrl;
    }

    public void setCourseWarePPTUrl(String courseWarePPTUrl) {
        this.courseWarePPTUrl = courseWarePPTUrl;
    }

    public String getCourseWarePDFUrl() {
        return courseWarePDFUrl;
    }

    public void setCourseWarePDFUrl(String courseWarePDFUrl) {
        this.courseWarePDFUrl = courseWarePDFUrl;
    }
}

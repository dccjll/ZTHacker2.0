package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/16.
 */

public class VideoInfoBean implements Serializable {
    /*" "videoList": [
      {
         "isNewRecord": true,
            "fileName": "飞行奥秘-手掷飞机",
            "videoSynopsis": "了解莱特兄弟的故事；学习固定翼飞机的飞行原理；探究尾翼在飞行中的作用。",
            "courseWareName": "飞行奥秘-手掷飞机",
            "fileUrl": "http://oxy5a0fop.bkt.clouddn.com/ltEhFbhi9bYpcREwCd5KBrrwP1A-",
            "fileUrlImg": "http://p3gq0kfyp.bkt.clouddn.com/img_pilot_videocover_1.jpg",
            "courseWareImgUrl": "http://p3gq0kfyp.bkt.clouddn.com/%E5%B0%81-001%20%E9%A3%9E%E8%A1%8C%E5%A5%A5%E7%A7%98-%E7%8C%8E%E9%B9%B0%E6%89%8B%E6%8E%B7%E9%A3%9E%E6%9C%BA.png",
            "courseWarePPTUrl": "http://p3gq0kfyp.bkt.clouddn.com/001%20%E9%A3%9E%E8%A1%8C%E5%A5%A5%E7%A7%98-%E7%8C%8E%E9%B9%B0%E6%89%8B%E6%8E%B7%E9%A3%9E%E6%9C%BA.pptx",
            "courseWarePDFUrl": "http://p3gq0kfyp.bkt.clouddn.com/001%20%E9%A3%9E%E8%A1%8C%E5%A5%A5%E7%A7%98-%E7%8C%8E%E9%B9%B0%E6%89%8B%E6%8E%B7%E9%A3%9E%E6%9C%BA.pdf"
      }
    ],
    "course": "金牌飞行员"*/

    public String id;
    public boolean isNewRecord;
    public String fileName;
    public String videoSynopsis;
    public String fileUrl;
    public String fileUrlImg;
    public String courseWareImgUrl;
    public String courseWarePPTUrl;
    public String courseWarePDFUrl;
    public String courseWareName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getCourseWareName() {
        return courseWareName;
    }

    public void setCourseWareName(String courseWareName) {
        this.courseWareName = courseWareName;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getVideoSynopsis() {
        return videoSynopsis;
    }

    public void setVideoSynopsis(String videoSynopsis) {
        this.videoSynopsis = videoSynopsis;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCourseWarePDFUrl() {
        return courseWarePDFUrl;
    }

    public void setCourseWarePDFUrl(String courseWarePDFUrl) {
        this.courseWarePDFUrl = courseWarePDFUrl;
    }
}

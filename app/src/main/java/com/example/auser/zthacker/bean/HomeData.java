package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/25.
 */

public class HomeData implements Serializable{
    private List<HomeClassTypeInfoBean> courseManage;
    private List<BannerInfoBean> bannerInfoManage;
    private List<NewsInfoBean> publishInfo;
    private List<PublishInfoList.PublishInfo> articleList;
    private List<TeachingResourseInfoBean> videoList;

    public List<HomeClassTypeInfoBean> getCourseManage() {
        return courseManage;
    }

    public void setCourseManage(List<HomeClassTypeInfoBean> courseManage) {
        this.courseManage = courseManage;
    }

    public List<BannerInfoBean> getBannerInfoManage() {
        return bannerInfoManage;
    }

    public void setBannerInfoManage(List<BannerInfoBean> bannerInfoManage) {
        this.bannerInfoManage = bannerInfoManage;
    }

    public List<NewsInfoBean> getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(List<NewsInfoBean> publishInfo) {
        this.publishInfo = publishInfo;
    }

    public List<PublishInfoList.PublishInfo> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<PublishInfoList.PublishInfo> articleList) {
        this.articleList = articleList;
    }

    public List<TeachingResourseInfoBean> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<TeachingResourseInfoBean> videoList) {
        this.videoList = videoList;
    }
}

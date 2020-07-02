package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/9/1.
 */

public class MainFoundBean implements Serializable{
    private List<NewsInfoBean> publishInfo;
    private List<BannerInfoBean> bannerInfoManage;

    public List<NewsInfoBean> getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(List<NewsInfoBean> publishInfo) {
        this.publishInfo = publishInfo;
    }

    public List<BannerInfoBean> getBannerInfoManage() {
        return bannerInfoManage;
    }

    public void setBannerInfoManage(List<BannerInfoBean> bannerInfoManage) {
        this.bannerInfoManage = bannerInfoManage;
    }
}

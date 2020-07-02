package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengk on 2017/8/28.
 */

public class MainCourseBean implements Serializable{
    private List<HomeClassTypeInfoBean> courseManage;
    private List<BannerInfoBean> bannerInfoManage;

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
}

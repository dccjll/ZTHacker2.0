package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-5.
 */

public class UserHomePageInfo implements Serializable{
     /* "sysAppUserName": "航模模式KKK",
        "sysAppUerImg": "http://192.168.3.110:8080/ckkj/userfiles/images/appUserInfo/appUser/3ac89c27-e348-41da-be56-2bfddfc8840e.jpg",
        "followCount": 1,
        "articlesCount": 6,
        "collectCount": 1,
        "followMeCount": 1
        "isFollow":"1"
      */
     private String sysAppUserName;
     private String sysAppUerImg;
     private int followCount;
     private int articlesCount;
     private int collectCount;
     private int followMeCount;
     private String isFollow;

    public String getSysAppUserName() {
        return sysAppUserName;
    }

    public void setSysAppUserName(String sysAppUserName) {
        this.sysAppUserName = sysAppUserName;
    }

    public String getSysAppUerImg() {
        return sysAppUerImg;
    }

    public void setSysAppUerImg(String sysAppUerImg) {
        this.sysAppUerImg = sysAppUerImg;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getArticlesCount() {
        return articlesCount;
    }

    public void setArticlesCount(int articlesCount) {
        this.articlesCount = articlesCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getFollowMeCount() {
        return followMeCount;
    }

    public void setFollowMeCount(int followMeCount) {
        this.followMeCount = followMeCount;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }
}

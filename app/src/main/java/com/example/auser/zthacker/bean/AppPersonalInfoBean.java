package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/30.
 */

public class AppPersonalInfoBean implements Serializable {
    /*"id": "8a0d8983f3a84d10a4d0ae25ea14099a",
      "isNewRecord": false,
      "createDate": "2017-08-07 16:49:10",
      "updateDate": "2017-08-30 15:24:20",
      "userName": "zzd",
      "phoneNumber": "234",
      "passWord": "123"
      "photo": "fsdfgsdfg"
      "sex": "男",
      "identity": "老师"
      "userStatus": "0",
        "articlesCount": 13,
        "collectCount": 5,
        "followCount": 2,
        "followMeCount": 1*/
    private String id;
    private boolean isNewRecord;
    private String createDate;
    private String updateDate;
    private String userName;
    private String phoneNumber;
    private String passWord;
    private String photo;
    private String sex;
    private String identity;
    private String userStatus;
    private int articlesCount;
    private int collectCount;
    private int followCount;
    private int followMeCount;
    private String sysAppUerImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
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

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getFollowMeCount() {
        return followMeCount;
    }

    public void setFollowMeCount(int followMeCount) {
        this.followMeCount = followMeCount;
    }

    public String getSysAppUerImg() {
        return sysAppUerImg;
    }

    public void setSysAppUerImg(String sysAppUerImg) {
        this.sysAppUerImg = sysAppUerImg;
    }
}

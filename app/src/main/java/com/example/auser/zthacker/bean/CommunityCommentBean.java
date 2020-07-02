package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/10/31.
 */

public class CommunityCommentBean implements Serializable{
     /*"id": "fbfe3b2b15614047847d2465e2c04c0e",
             "isNewRecord": false,
             "createDate": "2018-02-02 17:05:22",
             "updateDate": "2018-02-02 17:05:22",
             "communityId": "68dbcf055858406abc191a83fd8015a7",
             "content": "The first ",
             "sysAppUser": "16b20626e4084c48876b006e0714bd18",
             "userName": "张益达",
             "sysAppUerImg": "http://p09f4cre6.bkt.clouddn.com/FjwyLG3joQjvMuhX_--7CyI86dFK",
             "parentId": "521c0702e576452492081bc21059624a",
             "relevantId": "521c0702e576452492081bc21059624a",
             "sysAppUser2": "dcb5ca49cede4faea0f35d6120d583ca",
             "userName2": "yuanz",
             "sysAppUerImg2": "http://p09f4cre6.bkt.clouddn.com/b80444bbb8b24122b3c6b9e5c9dd0968.jpg",
             "commentType": "1",
             "dataIndex": 394
              */
    private String id;//评论ID
    private String isNewRecord;
    private String content;//评论内容
    private String createDate;//回复时间
    private String updateDate;
    private String communityId;
    private String commentType;
    private String sysAppUser;//评论人ID
    private String userName;//评论人姓名
    private String sysAppUerImg;//评论人头像图片地址
    private String parentId;//一级评论id
    private String sysAppUser2;//被谁回复评论人ID
    private String userName2;//被谁回复评论人姓名
    private String sysAppUerImg2;//被评论人头像图片地址
    private String commetsum;//二级评论个数
    private String relevantId;//父级评论id
    private int dataIndex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSysAppUser() {
        return sysAppUser;
    }

    public void setSysAppUser(String sysAppUser) {
        this.sysAppUser = sysAppUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSysAppUerImg() {
        return sysAppUerImg;
    }

    public void setSysAppUerImg(String sysAppUerImg) {
        this.sysAppUerImg = sysAppUerImg;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSysAppUser2() {
        return sysAppUser2;
    }

    public void setSysAppUser2(String sysAppUser2) {
        this.sysAppUser2 = sysAppUser2;
    }

    public String getUserName2() {
        return userName2;
    }

    public void setUserName2(String userName2) {
        this.userName2 = userName2;
    }

    public String getSysAppUerImg2() {
        return sysAppUerImg2;
    }

    public void setSysAppUerImg2(String sysAppUerImg2) {
        this.sysAppUerImg2 = sysAppUerImg2;
    }

    public String getCommetsum() {
        return commetsum;
    }

    public void setCommetsum(String commetsum) {
        this.commetsum = commetsum;
    }

    public String getRelevantId() {
        return relevantId;
    }

    public void setRelevantId(String relevantId) {
        this.relevantId = relevantId;
    }

    public String getIsNewRecord() {
        return isNewRecord;
    }

    public void setIsNewRecord(String isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }
}

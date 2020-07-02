package com.example.auser.zthacker.bean;

import org.w3c.dom.ProcessingInstruction;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017-11-9.
 */

public class PublishTextBean implements Serializable{
    /*"id": "a7abebf047dd412396112a7e4e692107",
            "isNewRecord": false,
            "createDate": "2017-11-09 16:42:44",
            "updateDate": "2017-11-09 16:42:44",
            "title": "发生的公司规范规定符合",
            "sysAppUser": "f108fd06ba9c45cea1015f2ff569a0ad",
            "content": "",
            "type": "0",
            "state": "0"
            "articleId":"3a5b7c1bf95b4865bb922c02643e37e9"*/
    private String id;
    private boolean isNewRecord;
    private String createDate;
    private String updateDate;
    private String title;
    private String sysAppUser;
    private String content;
    private String type;
    private String state;
    private String articleId;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSysAppUser() {
        return sysAppUser;
    }

    public void setSysAppUser(String sysAppUser) {
        this.sysAppUser = sysAppUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}

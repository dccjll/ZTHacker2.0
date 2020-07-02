package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/21.
 */

public class HomeTypeBean implements Serializable{
          /*"id": "d20a89ad1eef4571a1f4bd2633098766",
            "isNewRecord": false,
            "createDate": "2017-08-22 17:08:46",
            "updateDate": "2017-08-22 17:08:49",
            "propagatePic": "/ckkj/upload/u37.png",
            "propagateOrder": "2",
            "propagateLink": "www.baidu.com",
            "propagatePosition": "1",
            "pubTime": "2017-08-22 17:08:39",
            "propagateStatus": "0",
            "propagateTitle": "STEAM课程解决方案"
            "picUrl": "http://192.168.3.110:8080/ckkj/userfiles/images/appUserInfo/appUser/2a651ff9-d4e1-40fd-98aa-290ced25b373.jpg"
            "isRecycleview":false*/
    private String propagateTitle;
    private String propagateStatus;
    private String id;
    private String isNewRecord;
    private String createDate;
    private String updateDate;
    private String propagatePic;
    private String propagateOrder;
    private String propagateLink;
    private String propagatePosition;
    private String pubTime;
    private String picUrl;
    private boolean isRecycleview;
    private boolean isAddDivider;

    public String getPropagateTitle() {
        return propagateTitle;
    }

    public void setPropagateTitle(String propagateTitle) {
        this.propagateTitle = propagateTitle;
    }

    public String getPropagateStatus() {
        return propagateStatus;
    }

    public void setPropagateStatus(String propagateStatus) {
        this.propagateStatus = propagateStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsNewRecord() {
        return isNewRecord;
    }

    public void setIsNewRecord(String isNewRecord) {
        this.isNewRecord = isNewRecord;
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

    public String getPropagatePic() {
        return propagatePic;
    }

    public void setPropagatePic(String propagatePic) {
        this.propagatePic = propagatePic;
    }

    public String getPropagateOrder() {
        return propagateOrder;
    }

    public void setPropagateOrder(String propagateOrder) {
        this.propagateOrder = propagateOrder;
    }

    public String getPropagateLink() {
        return propagateLink;
    }

    public void setPropagateLink(String propagateLink) {
        this.propagateLink = propagateLink;
    }

    public String getPropagatePosition() {
        return propagatePosition;
    }

    public void setPropagatePosition(String propagatePosition) {
        this.propagatePosition = propagatePosition;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isRecycleview() {
        return isRecycleview;
    }

    public void setRecycleview(boolean recycleview) {
        isRecycleview = recycleview;
    }

    public boolean isAddDivider() {
        return isAddDivider;
    }

    public void setAddDivider(boolean addDivider) {
        isAddDivider = addDivider;
    }
}

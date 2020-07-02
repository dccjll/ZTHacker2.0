package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/25.
 */

public class BannerInfoBean implements Serializable {
    /*  "id": "e7b324f9a6704355bb34326f444dd503",
        "isNewRecord": false,
        "createDate": "2017-09-06 13:51:09",
        "updateDate": "2017-09-06 17:15:28",
        "pic": "012c9497d022408cabbb607fd7434219,",
        "pageOrder": "1",
        "url": "www.baidu.com",
        "pagePosition": "1",
        "uploadDate": "2017-09-06 13:51:09",
        "bannerStatus": "-1",
        "picUrl": "http://192.168.3.110:8080/ckkj/userfiles/images/banner/banner/679eb2ca-828e-4e32-900e-bde58e6be507.png"
        "bannerTitle":""*/
    private String id;
    private String isNewRecord;
    private String createDate;
    private String updateDate;
    private String pic;
    private String pageOrder;
    private String url;
    private String pagePosition;
    private String uploadDate;
    private String bannerStatus;
    private String picUrl;
    private String bannerTitle;

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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPageOrder() {
        return pageOrder;
    }

    public void setPageOrder(String pageOrder) {
        this.pageOrder = pageOrder;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getBannerStatus() {
        return bannerStatus;
    }

    public void setBannerStatus(String bannerStatus) {
        this.bannerStatus = bannerStatus;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }
}

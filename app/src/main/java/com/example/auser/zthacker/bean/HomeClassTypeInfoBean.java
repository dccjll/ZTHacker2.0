package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/25.
 */

public class HomeClassTypeInfoBean implements Serializable {
      /*"id": "0ceec8f00b414dbcbf9c09d1171209d9",
        "isNewRecord": false,
        "createDate": "2017-09-06 16:59:34",
        "updateDate": "2017-09-06 17:00:37",
        "courseName": "金牌飞行员测试",
        "listPic": "3c85fbf6bd814f699fe47298c7bc5ad9,",
        "courseSynopsis": "金牌飞行员",
        "video": "ca6646762a97458595b65af6ccef49cd,d7d52a0a4a9d43b2aae80744c304aedf,f0de60f9c1dc43f1b55b2d93b659f931,18507d1da6a24486a88b690f5a586d5f,",
        "pubTime": "2017-09-06 16:59:34",
        "courseStatus": "4",
         "addLink": "http://www.playsteam.cn/ckkj/appH5/page/fly/courseDetail.html",首页链接
         "materialLink": "http://www.playsteam.cn/ckkj/appH5/page/fly/courseDetail.html",教材链接
         "aidsLink": "http://www.playsteam.cn/ckkj/appH5/page/fly/courseDetail.html",教具链接
        "picUrl": "http://192.168.3.110:8080/ckkj/userfiles/images/course/course/c8e4aa98-b9f0-4d98-8dd3-8128655ad6e3.png",
        "attachList": [
          {
            "isNewRecord": true,
            "fileUrl": "http://192.168.3.110:8080/ckkj/userfiles/files/document/course/course/488180c2-0d60-479c-b663-185dc2e09ee2.mp4"
          },
          {
            "isNewRecord": true,
            "fileUrl": "http://192.168.3.110:8080/ckkj/userfiles/files/document/course/course/5eed5d66-4d06-4da8-a711-3111abc52997.mp4"
          },
          {
            "isNewRecord": true,
            "fileUrl": "http://192.168.3.110:8080/ckkj/userfiles/files/document/course/course/13a319bc-83f0-4296-a74d-2173eadb8bbd.mp4"
          },
          {
            "isNewRecord": true,
            "fileUrl": "http://192.168.3.110:8080/ckkj/userfiles/files/document/course/course/7e5f924f-8bc7-40d0-b2af-078e8f7fab90.mp4"
          }
        ]*/
    private String id;
    private String isNewRecord;
    private String createDate;
    private String updateDate;
    private String courseName;
    private String listPic;
    private String listOrder;
    private String addLink;
    private String courseSynopsis;
    private String pubTime;
    private String courseStatus;
    private String video;
    private String picUrl;
    private List<NewsInfoBean.PhotoAndVideoInfo> attachList;

    private String materialLink;
    private String aidsLink;

    public String getAddLink() {
        return addLink;
    }

    public void setAddLink(String addLink) {
        this.addLink = addLink;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getListPic() {
        return listPic;
    }

    public void setListPic(String listPic) {
        this.listPic = listPic;
    }

    public String getListOrder() {
        return listOrder;
    }

    public void setListOrder(String listOrder) {
        this.listOrder = listOrder;
    }

    public String getCourseSynopsis() {
        return courseSynopsis;
    }

    public void setCourseSynopsis(String courseSynopsis) {
        this.courseSynopsis = courseSynopsis;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<NewsInfoBean.PhotoAndVideoInfo> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<NewsInfoBean.PhotoAndVideoInfo> attachList) {
        this.attachList = attachList;
    }

    public String getMaterialLink() {
        return materialLink;
    }

    public void setMaterialLink(String materialLink) {
        this.materialLink = materialLink;
    }

    public String getAidsLink() {
        return aidsLink;
    }

    public void setAidsLink(String aidsLink) {
        this.aidsLink = aidsLink;
    }
}

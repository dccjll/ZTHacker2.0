package com.example.auser.zthacker.bean;

import com.example.auser.zthacker.utils.TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-27.
 */

public class CourseToolRecommendListBean implements Serializable {
    private String id;
    private boolean isNewRecord;
    private String createDate;
    private String updateDate;
    private String listPic;
    private String detailsPic;
    private String listOrder;
    private String addLink;
    private String synopsis;
    private String status;
    private String courseId;
    private String realiaTitle;
    private List<TeachingResourseInfoBean> realiaList;
    private List<NewsInfoBean> publishList;

    public List<String> getImagesList(String urls){
        List<String> imagesList = new ArrayList<>();
        if (!TextUtil.isNull(urls)){
            if (urls.contains(",")){
                String[] split = urls.split(",");
                for (int i = 0;i<split.length;i++){
                    if (!TextUtil.isNull(split[i])){
                        imagesList.add(split[i]);
                    }
                }
            }else {
                imagesList.add(urls);
            }

        }
        return imagesList;
    }

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

    public String getListPic() {
        return listPic;
    }

    public void setListPic(String listPic) {
        this.listPic = listPic;
    }

    public String getDetailsPic() {
        return detailsPic;
    }

    public void setDetailsPic(String detailsPic) {
        this.detailsPic = detailsPic;
    }

    public String getListOrder() {
        return listOrder;
    }

    public void setListOrder(String listOrder) {
        this.listOrder = listOrder;
    }

    public String getAddLink() {
        return addLink;
    }

    public void setAddLink(String addLink) {
        this.addLink = addLink;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getRealiaTitle() {
        return realiaTitle;
    }

    public void setRealiaTitle(String realiaTitle) {
        this.realiaTitle = realiaTitle;
    }

    public List<TeachingResourseInfoBean> getRealiaList() {
        return realiaList;
    }

    public void setRealiaList(List<TeachingResourseInfoBean> realiaList) {
        this.realiaList = realiaList;
    }

    public List<NewsInfoBean> getPublishList() {
        return publishList;
    }

    public void setPublishList(List<NewsInfoBean> publishList) {
        this.publishList = publishList;
    }
}

package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/15.
 */

public class NewsInfoBean implements Serializable {
    /* "id": "025587d401e24ec8b276d87bbfbf0012",
             "isNewRecord": false,
             "createDate": "2017-08-30 17:45:45",
             "articleTitle": "第三发过后还计划计划",
             "articleSource": "大房东说",
             "articleType": "0",
             "articleIntro": "阿发达的奋斗奋斗",
             "articleUrl": "https://www.baidu.com",
             "contentUrl": "http://192.168.3.110:8080/ckkj/m/app/publishInfo/content/info",
             "showType": "1",
             "attachList": [
     {
         "isNewRecord": true,
             "fileUrl": "http://192.168.3.110:8080/ckkj\\userfiles\\images\\publish\\publishInfo\\e8fb0028-7311-411b-86e8-1ccc4a19dba9.jpg"
     }
         ]
          "dataIndex": 11*/
    //isAddItemDecoration : 是否添加了分割线
    private String id;
    private String isNewRecord;
    private String createDate;
    private String articleTitle;
    private String articleSource;
    private String articleType;
    private String articleIntro;
    private String articleUrl;
    private String contentUrl;
    private String showType;
    private List<PhotoAndVideoInfo> attachList;
    private int likeCount;
    private String isLike;
    private int commentCount;
    private int dataIndex;


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

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleSource() {
        return articleSource;
    }

    public void setArticleSource(String articleSource) {
        this.articleSource = articleSource;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getArticleIntro() {
        return articleIntro;
    }

    public void setArticleIntro(String articleIntro) {
        this.articleIntro = articleIntro;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public List<PhotoAndVideoInfo> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<PhotoAndVideoInfo> attachList) {
        this.attachList = attachList;
    }

    public static class PhotoAndVideoInfo implements Serializable{
        public boolean isNewRecord;
        public String fileUrl;

        public boolean isNewRecord() {
            return isNewRecord;
        }

        public void setNewRecord(boolean newRecord) {
            isNewRecord = newRecord;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }
}

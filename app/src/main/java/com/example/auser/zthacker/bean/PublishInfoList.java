package com.example.auser.zthacker.bean;

import android.util.Log;

import com.example.auser.zthacker.adapter.FoundCommunityPicturesAdapter;
import com.example.auser.zthacker.utils.TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengkq on 2017/8/14.
 */

public class PublishInfoList implements Serializable{
    private List<PublishInfo> dataList;
    private List<BannerInfoBean> bannerInfoManage;
    private int dataIndex;

    public List<PublishInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<PublishInfo> dataList) {
        this.dataList = dataList;
    }

    public List<BannerInfoBean> getBannerInfoManage() {
        return bannerInfoManage;
    }

    public void setBannerInfoManage(List<BannerInfoBean> bannerInfoManage) {
        this.bannerInfoManage = bannerInfoManage;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public static class PublishInfo implements Serializable{
         /*"isAddDivider":false
         "id": "38a2cf8713164d9ba20c1ccc2b6e36e1",
                 "isNewRecord": false,
                 "createDate": "2017-11-09 16:01:16",
                 "updateDate": "2017-11-09 16:01:16",
                 "title": "标题",
                 "sysAppUser": "f108fd06ba9c45cea1015f2ff569a0ad",
                 "content": "内容内容......",
                 "type": "1",
                 "state": "0",
                 "imgIds": "fed623a134fd4f49901f2526e7b2a5f4adddb2ff66ac4b9399242dce6784dab9",
                 "collectCount": 0,
                 "likeCount": 0,
                 "commentCount": 0,
                 "isLike": 0,
                 "isCollect": 0,
                  "dataIndex": 4
                 "imgUrls": "http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/a6ff5775-fd47-4a55-9613-415fdceadf5b.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/b312a187-e858-409f-a797-c4129e588388.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/bc43f9ff-826a-4ad6-88ca-3b28e88a63c8.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/8d6ad74e-d763-4b38-ace2-acca339ce3de.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/d5d0ea72-af61-4ffe-b634-8b2791447a09.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/2aa58610-dbf7-4124-a4c1-88fe6c857ad9.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/77e0fed6-9b4b-450a-9850-6ba0f55ef608.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/1ce35bd5-ac34-4a45-ac17-cd3c9412a081.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/443e1b90-afac-4337-8ade-dafe9dd3ec2b.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/e7e76d3a-d966-4548-a9ad-7a3a9c292131.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/3b843a39-dfd3-4277-8626-579168440ae5.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/dfb6b64e-f5ad-4253-bede-a4cc61f8edec.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/16bfe1c7-1fec-4685-8754-368cad91dc30.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/3ce84d7f-f4a3-425e-84e1-f2f98e5fc1b1.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/0156fe94-3fca-46fd-8b24-9668b72e1cd9.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/07d7b547-49d1-40fb-a14b-237853da69a3.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/517c637a-5cec-4c86-9ec0-060f8351c3d6.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/2fc077ee-9adc-4ed5-b70b-7c6c86f317d0.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/5ffee859-f3b8-4b0a-96cb-ec6b295a6cab.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/79f2495a-8543-4f0e-9d4a-196710b70b44.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/31f1de34-aa6c-4a05-8a84-50f297594cdd.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/39af7a24-fef1-44eb-ab18-864dd0bc92e2.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/dd43ba33-ea30-4f78-b215-6fb0530667d7.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/b9b31d22-9252-47ee-8a40-7b342521678b.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/cc10ae4c-ae75-4e1a-9ba8-f0cd4eda93bb.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/68edca32-13be-43be-8f56-435058b3424a.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/887df29c-9f12-41d3-9bb3-de54e71ab474.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/a61b5e98-f8f2-4710-8594-a72649c5d7b4.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/bd08c9fa-00ac-4549-8539-e527e0e539da.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/8ba6e675-57cf-4076-a6c3-9df615726fef.jpg" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/81b0fcf9-4b51-4f08-8d3b-512cd1872d09.JPEG" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/0e8e1fd9-ce21-4c86-a8e1-633ff76eebf0.png" +
                 ",http://192.168.3.110:8080/ckkj/userfiles/images/articles/article/bcdbde33-37c4-4c67-9040-a758a5ea9c11.png"*/private FoundCommunityPicturesAdapter adapter;
        private boolean isAddDivider;
        private String id;
        private boolean isNewRecord;
        private String createDate;
        private String updateDate;
        private String title;
        private String sysAppUser;
        private String sysAppUserName;
        private String imgIds;
        private String imgUrls;
        private String sysAppUerImg;
        private String content;
        private String type;
        private String state;
        private String videoUrl;
        private String videoIds;
        private String pubTime;
        private int collectCount;
        private int likeCount;
        private int isLike;
        private int isCollect;
        private int commentCount;
        private int isFollow;
        private int dataIndex;


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

        public FoundCommunityPicturesAdapter getAdapter() {
            return adapter;
        }

        public void setAdapter(FoundCommunityPicturesAdapter adapter) {
            this.adapter = adapter;
        }

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        public boolean isAddDivider() {
            return isAddDivider;
        }

        public void setAddDivider(boolean addDivider) {
            isAddDivider = addDivider;
        }

        public String getImgUrls() {
            return imgUrls;
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

        public String getImgIds() {
            return imgIds;
        }

        public void setImgIds(String imgIds) {
            this.imgIds = imgIds;
        }

        public void setImgUrls(String imgUrls) {
            this.imgUrls = imgUrls;
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

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getPubTime() {
            return pubTime;
        }

        public void setPubTime(String pubTime) {
            this.pubTime = pubTime;
        }

        public int getCollectCount() {
            return collectCount;
        }

        public void setCollectCount(int collectCount) {
            this.collectCount = collectCount;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public int getIsLike() {
            return isLike;
        }

        public void setIsLike(int isLike) {
            this.isLike = isLike;
        }

        public int getIsCollect() {
            return isCollect;
        }

        public void setIsCollect(int isCollect) {
            this.isCollect = isCollect;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public String getVideoIds() {
            return videoIds;
        }

        public void setVideoIds(String videoIds) {
            this.videoIds = videoIds;
        }

        public int getDataIndex() {
            return dataIndex;
        }

        public void setDataIndex(int dataIndex) {
            this.dataIndex = dataIndex;
        }
    }
}

package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-12.
 */

public class MsgZanListBean implements Serializable{
     /* "articleId": "040b593a4ed5483fa6ecd12d35ed2ec1",
            "likeList": [
                "3242"
            ]
             "dataIndex": 128*/

     private String articleId;
     private List<String>likeList;
     private int dataIndex;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public List<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<String> likeList) {
        this.likeList = likeList;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }
}

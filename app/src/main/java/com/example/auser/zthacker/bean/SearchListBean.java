package com.example.auser.zthacker.bean;

import com.example.auser.zthacker.ui.activity.search.SearchListActivity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-27.
 */

public class SearchListBean implements Serializable{
    private List<PublishInfoList.PublishInfo> articleList;
    private List<NewsInfoBean> publishInfoList;

    public List<PublishInfoList.PublishInfo> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<PublishInfoList.PublishInfo> articleList) {
        this.articleList = articleList;
    }

    public List<NewsInfoBean> getPublishInfoList() {
        return publishInfoList;
    }

    public void setPublishInfoList(List<NewsInfoBean> publishInfoList) {
        this.publishInfoList = publishInfoList;
    }
}

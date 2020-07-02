package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-15.
 */

public class MsgAdapterListBean implements Serializable{
    private List<CommunityCommentBean> commetList;
    private PublishInfoList.PublishInfo articles;
    private MsgZanListBean zanBean;

    public List<CommunityCommentBean> getCommetList() {
        return commetList;
    }

    public void setCommetList(List<CommunityCommentBean> commetList) {
        this.commetList = commetList;
    }

    public PublishInfoList.PublishInfo getArticles() {
        return articles;
    }

    public void setArticles(PublishInfoList.PublishInfo articles) {
        this.articles = articles;
    }

    public MsgZanListBean getZanBean() {
        return zanBean;
    }

    public void setZanBean(MsgZanListBean zanBean) {
        this.zanBean = zanBean;
    }
}

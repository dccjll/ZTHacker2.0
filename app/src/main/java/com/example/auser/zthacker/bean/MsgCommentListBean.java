package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dell on 2018-2-5.
 */

public class MsgCommentListBean implements Serializable {
    private List<CommunityCommentBean> commentList;
    private List<PublishInfoList.PublishInfo> articleList;

    public List<CommunityCommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommunityCommentBean> commentList) {
        this.commentList = commentList;
    }

    public List<PublishInfoList.PublishInfo> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<PublishInfoList.PublishInfo> articleList) {
        this.articleList = articleList;
    }
}

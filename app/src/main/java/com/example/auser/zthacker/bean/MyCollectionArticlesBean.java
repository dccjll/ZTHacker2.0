package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengkq on 2017-12-8.
 */

public class MyCollectionArticlesBean implements Serializable{
    private List<PublishInfoList.PublishInfo> dataList;

    public List<PublishInfoList.PublishInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<PublishInfoList.PublishInfo> dataList) {
        this.dataList = dataList;
    }
}

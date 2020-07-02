package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2018-3-2.
 */

public class MsgNoneWatchedCountBean implements Serializable {
     /*"likeCount": 5,
       "coummentCount": 3*/
     private int likeCount;
     private int coummentCount;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCoummentCount() {
        return coummentCount;
    }

    public void setCoummentCount(int coummentCount) {
        this.coummentCount = coummentCount;
    }
}

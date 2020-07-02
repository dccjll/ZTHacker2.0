package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017-12-8.
 */

public class UserFollowBean implements Serializable{
     /*"id": "45d086bfd3ef48dd98b5acb0bc1aa5ca",
             "isNewRecord": false,
             "sysAppUser": {
        "id": "f108fd06ba9c45cea1015f2ff569a0ad",
                "isNewRecord": false
    },
            "sysAppUser2": {
        "id": "06fdbf07c11344768dd384c4832194f3",
                "isNewRecord": false,
                "userName": "袁总"
    },
            "isFollow": "0"*/
    private String id;
    private boolean isNewRecord;
    private AppPersonalInfoBean sysAppUser;
    private AppPersonalInfoBean sysAppUser2;
    private String isFollow;

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

    public AppPersonalInfoBean getSysAppUser() {
        return sysAppUser;
    }

    public void setSysAppUser(AppPersonalInfoBean sysAppUser) {
        this.sysAppUser = sysAppUser;
    }

    public AppPersonalInfoBean getSysAppUser2() {
        return sysAppUser2;
    }

    public void setSysAppUser2(AppPersonalInfoBean sysAppUser2) {
        this.sysAppUser2 = sysAppUser2;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }
}

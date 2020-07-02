package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/7.
 */

public class TypeSelectBean implements Serializable{
    private String name;
    private boolean isSelect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public TypeSelectBean(String name, boolean isSelect) {
        this.name = name;
        this.isSelect = isSelect;
    }
}

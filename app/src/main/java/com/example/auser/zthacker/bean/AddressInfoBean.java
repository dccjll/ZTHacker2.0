package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/7.
 */

public class AddressInfoBean implements Serializable{
    private String name;
    private String phone;
    private String address;
    private boolean isSelect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

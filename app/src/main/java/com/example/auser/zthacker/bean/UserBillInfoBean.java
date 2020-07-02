package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/11.
 */

public class UserBillInfoBean implements Serializable{
    private String companyName;
    private String identificationCode;
    private String registerAddress;
    private String registerTelephone;
    private String openBank;
    private String bankCard;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getRegisterTelephone() {
        return registerTelephone;
    }

    public void setRegisterTelephone(String registerTelephone) {
        this.registerTelephone = registerTelephone;
    }

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }
}

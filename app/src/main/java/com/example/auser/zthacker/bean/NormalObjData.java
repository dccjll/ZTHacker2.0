package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by ZYS on 2017/4/22.
 */

public class NormalObjData<T> implements Serializable {
    private String error;
    private T data;
    private boolean result;
    private String codeDesc;
    private String msg;
    private String code;
    private int dataIndex;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    @Override
    public String toString() {
        return "NormalObjData{" +
                "error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}

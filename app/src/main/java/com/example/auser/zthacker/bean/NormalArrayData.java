package com.example.auser.zthacker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZYS on 2017/4/22.
 */

public class NormalArrayData<T> implements Serializable {
    private String error;
    private boolean result;
    private String codeDesc;
    private String msg;
    private String code;
    private NormalArrayData_Data<T> data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public NormalArrayData_Data getData() {
        return data;
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

    public void setData(NormalArrayData_Data<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NormalArrayData{" +
                "error='" + error + '\'' +
                ", data=" + data +
                '}';
    }

    public class NormalArrayData_Data<T> implements Serializable{
        private List<T> data;

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "NormalArrayData_Data{" +
                    "data=" + data +
                    '}';
        }
    }
}

package com.example.auser.zthacker.bean;

import java.io.Serializable;

/**
 * Created by ZYS on 2017/4/22.
 */

public class NormalBooleanData implements Serializable {
    private String error;
    private NormalBooleanData_Data data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public NormalBooleanData_Data getData() {
        return data;
    }

    public void setData(NormalBooleanData_Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NormalBooleanData{" +
                "error='" + error + '\'' +
                ", data=" + data +
                '}';
    }

    public class NormalBooleanData_Data implements Serializable{
        private int issuccess;
        private int status;

        @Override
        public String toString() {
            return "NormalBooleanData_Data{" +
                    "issuccess=" + issuccess +
                    ", status=" + status +
                    '}';
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIssuccess() {
            return issuccess;
        }

        public void setIssuccess(int issuccess) {
            this.issuccess = issuccess;
        }
    }
}

package com.example.auser.zthacker.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by zhengkq on 2017/8/10.
 */

public class PublishImageBean implements Serializable {
    private String path;
    private File file;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

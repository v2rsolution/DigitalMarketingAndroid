package com.wscubetech.seovideotutorials.model;

/**
 * Created by wscubetech on 4/10/16.
 */
public class KeyValueModel {
    String key = "", value = "";
    boolean isFile = false;

    public KeyValueModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValueModel(String key, String value, boolean isFile) {
        this(key, value);
        this.isFile = isFile;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

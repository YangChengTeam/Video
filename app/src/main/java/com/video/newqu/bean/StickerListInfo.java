package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/11
 * 所有的贴纸类型列表
 */

public class StickerListInfo implements Serializable{

    /**
     * data : [{"id":"157","name":"情侣"},{"id":"158","name":"甜蜜"},{"id":"159","name":"幸福"}]
     * code : 1
     */

    private int code;
    /**
     * id : 157
     * name : 情侣
     */

    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

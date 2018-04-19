package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/26 19:31
 * 话题
 */
public class TopicList implements Serializable{

    /**
     * code : 1
     * data : [{"id":"1","topic":"今日打卡"},{"id":"2","topic":"颜值担当"}]
     */

    private int code;
    /**
     * id : 1
     * topic : 今日打卡
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
        private String topic;
        private boolean isSelector;


        public boolean isSelector() {
            return isSelector;
        }

        public void setSelector(boolean selector) {
            isSelector = selector;
        }



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}

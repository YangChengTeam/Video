package com.video.newqu.bean;

import com.video.newqu.comadapter.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/11.
 * 字幕Info
 */

public class CaptionsInfo implements Serializable{

    /**
     * code : 1
     * data : [{"add_time":"0","bottom":"100","caption_id":"56","caption_title":"大西瓜","caption_url":"http://app.nq6.com//Upload/Picture/2017-09-14/59ba0103ac80b.png","left":"100","right":"100","top":"100"},{"add_time":"0","bottom":"100","caption_id":"57","caption_title":"花花","caption_url":"http://app.nq6.com//Upload/Picture/2017-09-14/59ba011952cdb.png","left":"100","right":"100","top":"100"},{"add_time":"0","bottom":"100","caption_id":"58","caption_title":"粉红","caption_url":"http://app.nq6.com//Upload/Picture/2017-09-14/59ba013def0dc.png","left":"100","right":"100","top":"100"}]
     */

    private int code;
    /**
     * add_time : 0
     * bottom : 100
     * caption_id : 56
     * caption_title : 大西瓜
     * caption_url : http://app.nq6.com//Upload/Picture/2017-09-14/59ba0103ac80b.png
     * left : 100
     * right : 100
     * top : 100
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

    public static class DataBean implements MultiItemEntity,Serializable{
        private String add_time;
        private String bottom;
        private String caption_id;
        private String caption_title;
        private String caption_url;
        private String left;
        private String right;
        private String top;
        private int itemType;

        public boolean isDownloading() {
            return isDownloading;
        }

        public void setDownloading(boolean downloading) {
            isDownloading = downloading;
        }

        private boolean isDownloading;

        @Override
        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getBottom() {
            return bottom;
        }

        public void setBottom(String bottom) {
            this.bottom = bottom;
        }

        public String getCaption_id() {
            return caption_id;
        }

        public void setCaption_id(String caption_id) {
            this.caption_id = caption_id;
        }

        public String getCaption_title() {
            return caption_title;
        }

        public void setCaption_title(String caption_title) {
            this.caption_title = caption_title;
        }

        public String getCaption_url() {
            return caption_url;
        }

        public void setCaption_url(String caption_url) {
            this.caption_url = caption_url;
        }

        public String getLeft() {
            return left;
        }

        public void setLeft(String left) {
            this.left = left;
        }

        public String getRight() {
            return right;
        }

        public void setRight(String right) {
            this.right = right;
        }

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }
    }
}

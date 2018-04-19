package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 音乐选择界面菜单
 */

public class MediaMusicHomeMenu implements Serializable{

    /**
     * code : 1
     * data : [{"cover":"http://sc.wk2.com/upload/category/2017-11-09/5a03f6725eba8.jpg","id":"7","name":"经典","pid":"1","sort":"1"},{"cover":"http://sc.wk2.com/upload/category/2017-11-09/5a03f68c840de.jpg","id":"8","name":"流行","pid":"1","sort":"2"},{"cover":"http://sc.wk2.com/upload/category/2017-11-09/5a03f6ae362d0.jpg","id":"9","name":"欧美","pid":"1","sort":"3"},{"cover":"http://sc.wk2.com/upload/category/2017-11-09/5a03f6c203fd7.jpg","id":"10","name":"日韩","pid":"1","sort":"4"}]
     */


    /**
     * cover : http://sc.wk2.com/upload/category/2017-11-09/5a03f6725eba8.jpg
     * id : 7
     * name : 经典
     * pid : 1
     * sort : 1
     */
    private int code;
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

    public static class DataBean implements Serializable {
        private String cover;
        private String id;
        private String name;
        private String pid;
        private String sort;
        private int itemType;
        private int iconID;


        public int getIconID() {
            return iconID;
        }

        public void setIconID(int iconID) {
            this.iconID = iconID;
        }


        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

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

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
    }
}

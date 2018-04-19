package com.video.newqu.bean;

import com.video.newqu.comadapter.entity.MultiItemEntity;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/22 15:11
 */

public class MessageInfo implements Serializable{

    public static final int ITEM_1=1;//关注
    public static final int ITEM_2=2;//收藏
    public static final int ITEM_3=3;//留言
    public static final int ITEM_4=4;//二次留言


    private int code;
    private String msg;
    /**
     * add_time : 1498180635
     * is_follow : 1
     * itemType : 1
     * logo : http://q.qlogo.cn/qqapp/1106176094/8F67981DDF30F77C26E17B3EFA9A95EA/100
     * nickname : ☀️梦一场
     * type : 1
     * user_id : 1060152
     */

    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean implements Serializable,MultiItemEntity {

        private String add_time;
        private int is_follow;
        private int itemType;
        private String logo;
        private String nickname;
        private String type;
        private String user_id;
        private String comment;
        private String cover;
        private String desp;
        private String video_id;
        private boolean isRead;//是否已读
        private String from_nickname;


        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }

        public String getFrom_nickname() {
            return from_nickname;
        }

        public void setFrom_nickname(String from_nickname) {
            this.from_nickname = from_nickname;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public int getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }

        @Override
        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDesp() {
            return desp;
        }

        public void setDesp(String desp) {
            this.desp = desp;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }
    }
}

package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/5 15:45
 * 我的关注者列表
 */
public class FollowUserList implements Serializable{


    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean  implements Serializable{
        private String count;
        /**
         * user_id : 1060150
         * fans_user_id : 1060153
         * nickname : 菲菲
         * logo : /Public/icon/default_logo.jpg
         * rank : 1
         * signature : 我坚持我的风格 我活在我的世界
         * image_bg : /Public/icon/tara.png
         * gender : 男
         * fans_nickname : 梦一场
         * is_follow : 1
         * both_fans : 0
         */

        private List<ListBean> list;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean  implements Serializable{
            private String user_id;
            private String fans_user_id;
            private String nickname;
            private String logo;
            private String rank;
            private String signature;
            private String image_bg;
            private String gender;
            private String fans_nickname;
            private int is_follow;
            private int both_fans;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getFans_user_id() {
                return fans_user_id;
            }

            public void setFans_user_id(String fans_user_id) {
                this.fans_user_id = fans_user_id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getRank() {
                return rank;
            }

            public void setRank(String rank) {
                this.rank = rank;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getImage_bg() {
                return image_bg;
            }

            public void setImage_bg(String image_bg) {
                this.image_bg = image_bg;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getFans_nickname() {
                return fans_nickname;
            }

            public void setFans_nickname(String fans_nickname) {
                this.fans_nickname = fans_nickname;
            }

            public int getIs_follow() {
                return is_follow;
            }

            public void setIs_follow(int is_follow) {
                this.is_follow = is_follow;
            }

            public int getBoth_fans() {
                return both_fans;
            }

            public void setBoth_fans(int both_fans) {
                this.both_fans = both_fans;
            }
        }
    }
}

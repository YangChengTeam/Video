package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/1 14:57
 * 粉丝列表  //image_bg
 */
public class FansInfo implements Serializable{



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
         * user_id : 1060153
         * fans_user_id : 1060292
         * fans_nickname : 黄天宇
         * signature : 锄禾日当午 汗滴禾下土
         * image_bg : http://192.168.80.110/Upload/Picture/2017-06-13/593f80bc09be4.jpg
         * logo : http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0
         * rank : 1
         * nickname : 测试的小明
         * is_fans : 1
         * both_fans : 1
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
            private String fans_nickname;
            private String signature;
            private String image_bg;
            private String logo;
            private String rank;
            private String nickname;
            private int is_fans;
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

            public String getFans_nickname() {
                return fans_nickname;
            }

            public void setFans_nickname(String fans_nickname) {
                this.fans_nickname = fans_nickname;
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

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getIs_fans() {
                return is_fans;
            }

            public void setIs_fans(int is_fans) {
                this.is_fans = is_fans;
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

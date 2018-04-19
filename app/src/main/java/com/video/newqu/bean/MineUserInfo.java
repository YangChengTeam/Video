package com.video.newqu.bean;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/6/1 18:29
 * 个人中心用户基本信息
 */
public class MineUserInfo implements Serializable {

    /**
     * code : 1
     * data : {"info":{"city":"武汉","collect_times":"8","dig_count":"4","fans":"4","follows":"2","gender":"男","goldcoin":"0","id":"1060153","image_bg":"http://192.168.80.110/Upload/Picture/2017-06-15/59424b35ad6b8.jpg","imeil":"862393036118699","is_follow":0,"login_time":"1495762711","login_type":"1","logo":"http://192.168.80.110/Upload/Picture/2017-06-12/593df13b9c956.jpg","nickname":"测试的小明","open_id":"8F67981DDF30F77C26E17B3EFA9A95EA","phone":"","province":"湖北","rank":"1","reg_time":"1495762711","signature":"","status":"0","username":"","video_count":"6"}}
     * msg : 获取数据成功
     */

    private int code;
    /**
     * info : {"city":"武汉","collect_times":"8","dig_count":"4","fans":"4","follows":"2","gender":"男","goldcoin":"0","id":"1060153","image_bg":"http://192.168.80.110/Upload/Picture/2017-06-15/59424b35ad6b8.jpg","imeil":"862393036118699","is_follow":0,"login_time":"1495762711","login_type":"1","logo":"http://192.168.80.110/Upload/Picture/2017-06-12/593df13b9c956.jpg","nickname":"测试的小明","open_id":"8F67981DDF30F77C26E17B3EFA9A95EA","phone":"","province":"湖北","rank":"1","reg_time":"1495762711","signature":"","status":"0","username":"","video_count":"6"}
     */

    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable{
        /**
         * city : 武汉
         * collect_times : 8
         * dig_count : 4
         * fans : 4
         * follows : 2
         * gender : 男
         * goldcoin : 0
         * id : 1060153
         * image_bg : http://192.168.80.110/Upload/Picture/2017-06-15/59424b35ad6b8.jpg
         * imeil : 862393036118699
         * is_follow : 0
         * login_time : 1495762711
         * login_type : 1
         * logo : http://192.168.80.110/Upload/Picture/2017-06-12/593df13b9c956.jpg
         * nickname : 测试的小明
         * open_id : 8F67981DDF30F77C26E17B3EFA9A95EA
         * phone :
         * province : 湖北
         * rank : 1
         * reg_time : 1495762711
         * signature :
         * status : 0
         * username :
         * video_count : 6
         */

        private InfoBean info;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean implements Serializable{
            private String city;
            private String collect_times;
            private String dig_count;
            private String fans;
            private String follows;
            private String gender;
            private String goldcoin;
            private String id;
            private String image_bg;
            private String imeil;
            private int is_follow;
            private String login_time;
            private String login_type;
            private String logo;
            private String nickname;
            private String open_id;
            private String phone;
            private String province;
            private String rank;
            private String reg_time;
            private String signature;
            private String status;
            private String username;
            private String video_count;
            private int msgCount;

            public int getMsgCount() {
                return msgCount;
            }

            public void setMsgCount(int msgCount) {
                this.msgCount = msgCount;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCollect_times() {
                return collect_times;
            }

            public void setCollect_times(String collect_times) {
                this.collect_times = collect_times;
            }

            public String getDig_count() {
                return dig_count;
            }

            public void setDig_count(String dig_count) {
                this.dig_count = dig_count;
            }

            public String getFans() {
                return fans;
            }

            public void setFans(String fans) {
                this.fans = fans;
            }

            public String getFollows() {
                return follows;
            }

            public void setFollows(String follows) {
                this.follows = follows;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getGoldcoin() {
                return goldcoin;
            }

            public void setGoldcoin(String goldcoin) {
                this.goldcoin = goldcoin;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImage_bg() {
                return image_bg;
            }

            public void setImage_bg(String image_bg) {
                this.image_bg = image_bg;
            }

            public String getImeil() {
                return imeil;
            }

            public void setImeil(String imeil) {
                this.imeil = imeil;
            }

            public int getIs_follow() {
                return is_follow;
            }

            public void setIs_follow(int is_follow) {
                this.is_follow = is_follow;
            }

            public String getLogin_time() {
                return login_time;
            }

            public void setLogin_time(String login_time) {
                this.login_time = login_time;
            }

            public String getLogin_type() {
                return login_type;
            }

            public void setLogin_type(String login_type) {
                this.login_type = login_type;
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

            public String getOpen_id() {
                return open_id;
            }

            public void setOpen_id(String open_id) {
                this.open_id = open_id;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getRank() {
                return rank;
            }

            public void setRank(String rank) {
                this.rank = rank;
            }

            public String getReg_time() {
                return reg_time;
            }

            public void setReg_time(String reg_time) {
                this.reg_time = reg_time;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getVideo_count() {
                return video_count;
            }

            public void setVideo_count(String video_count) {
                this.video_count = video_count;
            }
        }
    }
}

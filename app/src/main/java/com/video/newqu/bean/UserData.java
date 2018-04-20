package com.video.newqu.bean;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/5/24 15:19
 * 登录成功
 */
public class UserData implements Serializable{

    private int code;
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
         * gender : 男
         * id : 53
         * imeil : 862393036118699
         * logo : http://q.qlogo.cn/qqapp/1106176094/8F67981DDF30F77C26E17B3EFA9A95EA/100
         * nickname : ☀️梦一场
         * open_id : 8F67981DDF30F77C26E17B3EFA9A95EA
         * province : 湖北
         * signature :
         * type : 1
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
            private String gender;
            private String id;
            private String imeil;
            private String image_bg;
            private String logo;
            private String nickname;
            private String open_id;
            private String province;
            private String signature;
            private String type;
            private String login_type;
            private String reg_time;
            private String status;
            private String phone;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }


            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getImage_bg() {
                return image_bg;
            }

            public void setImage_bg(String image_bg) {
                this.image_bg = image_bg;
            }

            public String getReg_time() {
                return reg_time;
            }

            public void setReg_time(String reg_time) {
                this.reg_time = reg_time;
            }


            public String getLogin_type() {
                return login_type;
            }

            public void setLogin_type(String login_type) {
                this.login_type = login_type;
            }



            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImeil() {
                return imeil;
            }

            public void setImeil(String imeil) {
                this.imeil = imeil;
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

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}

package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017/6/4 12:07
 */
public class SingComentInfo {


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

    public static class DataBean {
        /**
         * comment : udbxhxj
         * comment_id : 42
         * encrypt_response : true
         * logo : http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0
         * nickname : 黄天宇
         * to_nickname : ☀️梦一场
         * to_user_id : 1060152
         * user_id : 1060156
         * video_id : 1
         */

        private InfoBean info;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            private String comment;
            private String comment_id;
            private String encrypt_response;
            private String logo;
            private String nickname;
            private String to_nickname;
            private String to_user_id;
            private String user_id;
            private String video_id;
            private String add_time;
            private String id;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }


            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }



            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public String getComment_id() {
                return comment_id;
            }

            public void setComment_id(String comment_id) {
                this.comment_id = comment_id;
            }

            public String getEncrypt_response() {
                return encrypt_response;
            }

            public void setEncrypt_response(String encrypt_response) {
                this.encrypt_response = encrypt_response;
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

            public String getTo_nickname() {
                return to_nickname;
            }

            public void setTo_nickname(String to_nickname) {
                this.to_nickname = to_nickname;
            }

            public String getTo_user_id() {
                return to_user_id;
            }

            public void setTo_user_id(String to_user_id) {
                this.to_user_id = to_user_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }
        }
    }
}

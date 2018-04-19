package com.video.newqu.bean;

import com.video.newqu.comadapter.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/25 9:57
 * 评论列表
 */
public class ComentList implements Serializable{

    /**
     * code : 1
     * msg : 获取视频评论列表成功
     * data : {"comment_list":[{"id":"1060158","user_id":"1060150","video_id":"1","comment":"喜欢你","add_time":"1496225094","nickname":"菲菲"},{"id":"1060157","user_id":"1060150","video_id":"1","comment":"喜欢你","add_time":"1496225091","nickname":"菲菲"},{"id":"1060156","user_id":"1060150","video_id":"1","comment":"喜欢你","add_time":"1496222623","nickname":"菲菲"}],"count":"17"}
     */

    private int code;
    private String msg;
    /**
     * comment_list : [{"id":"1060158","user_id":"1060150","video_id":"1","comment":"喜欢你","add_time":"1496225094","nickname":"菲菲"},{"id":"1060157","user_id":"1060150","video_id":"1","comment":"喜欢你","add_time":"1496225091","nickname":"菲菲"},{"id":"1060156","user_id":"1060150","video_id":"1","comment":"喜欢你","add_time":"1496222623","nickname":"菲菲"}]
     * count : 17
     */

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

    public static class DataBean implements Serializable{
        private String count;
        /**
         * id : 1060158
         * user_id : 1060150
         * video_id : 1
         * comment : 喜欢你
         * add_time : 1496225094
         * nickname : 菲菲
         */

        private List<CommentListBean> comment_list;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public List<CommentListBean> getComment_list() {
            return comment_list;
        }

        public void setComment_list(List<CommentListBean> comment_list) {
            this.comment_list = comment_list;
        }

        public static class CommentListBean implements MultiItemEntity ,Serializable{

            private String comment_id;
            private String encrypt_response;
            private String to_nickname;
            private String to_user_id;
            private String id;
            private String user_id;
            private String video_id;
            private String comment;
            private String add_time;
            private String nickname;
            private String logo;

            public String getTo_user_id() {
                return to_user_id;
            }

            public void setTo_user_id(String to_user_id) {
                this.to_user_id = to_user_id;
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

            public String getTo_nickname() {
                return to_nickname;
            }

            public void setTo_nickname(String to_nickname) {
                this.to_nickname = to_nickname;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public void setvideo_id(String video_id) {
                this.video_id = video_id;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            @Override
            public int getItemType() {
                return 0;
            }
        }
    }
}

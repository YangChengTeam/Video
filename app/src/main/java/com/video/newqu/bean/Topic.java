package com.video.newqu.bean;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/14 12:20
 */
public class Topic {

    /**
     * code : 0
     * data : {"video_list":[{"add_time":"1499942279","cover":"http://app.nq6.com/Upload/Cover/1060150/cb0cda9513cdd9c7be6160ff04da466e_100.jpg","desp":"#萌宠##搞笑#","id":"313","is_follow":0,"is_hot":"0","is_interest":0,"type":"3","user_id":"1060150","video_id":"313"},{"add_time":"1499940280","cover":"http://app.nq6.com/Upload/Cover/1060150/12ceadb350334b66881705437ddd431b_38.jpg","desp":"#萌宠#","id":"307","is_follow":0,"is_hot":"0","is_interest":0,"type":"3","user_id":"1060150","video_id":"307"},{"add_time":"1499940108","cover":"http://app.nq6.com/Upload/Cover/1060150/fbc2ac2678dc28faca37dfd035fc72fa_88.jpg","desp":"#萌宠##汪星人#","id":"305","is_follow":0,"is_hot":"0","is_interest":0,"type":"3","user_id":"1060150","video_id":"305"},{"add_time":"1499938743","cover":"http://app.nq6.com/Upload/Cover/1060150/7fef617d98c24f4df915a45367248f56_38.jpg","desp":"#萌宠#","id":"301","is_follow":0,"is_hot":"0","is_interest":0,"type":"3","user_id":"1060150","video_id":"301"},{"add_time":"1499934284","cover":"http://app.nq6.com/Upload/Cover/1060150/13bdf8f7cd89aca3480a2608c73c7444_25.jpg","desp":"#哈士奇##萌宠#","id":"300","is_follow":0,"is_hot":"0","is_interest":0,"type":"2","user_id":"1060150","video_id":"300"}]}
     */

    private int code;
    private DataBean data;

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

    public static class DataBean {
        /**
         * add_time : 1499942279
         * cover : http://app.nq6.com/Upload/Cover/1060150/cb0cda9513cdd9c7be6160ff04da466e_100.jpg
         * desp : #萌宠##搞笑#
         * id : 313
         * is_follow : 0
         * is_hot : 0
         * is_interest : 0
         * type : 3
         * user_id : 1060150
         * video_id : 313
         */

        private List<VideoListBean> video_list;

        public List<VideoListBean> getVideo_list() {
            return video_list;
        }

        public void setVideo_list(List<VideoListBean> video_list) {
            this.video_list = video_list;
        }

        public static class VideoListBean {
            private String add_time;
            private String cover;
            private String desp;
            private String id;
            private int is_follow;
            private String is_hot;
            private int is_interest;
            private String type;
            private String user_id;
            private String video_id;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
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

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getIs_follow() {
                return is_follow;
            }

            public void setIs_follow(int is_follow) {
                this.is_follow = is_follow;
            }

            public String getIs_hot() {
                return is_hot;
            }

            public void setIs_hot(String is_hot) {
                this.is_hot = is_hot;
            }

            public int getIs_interest() {
                return is_interest;
            }

            public void setIs_interest(int is_interest) {
                this.is_interest = is_interest;
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

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }
        }
    }
}

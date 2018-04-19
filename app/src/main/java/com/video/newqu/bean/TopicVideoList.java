package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/27 17:44
 */
public class TopicVideoList implements Serializable{

    /**
     * code : 1
     * data : {"video_list":[{"add_time":"1499932238","collect_times":"0","comment_list":[{"add_time":"1500015191","comment":"什么","id":"136","logo":"http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg","nickname":"皇太极","status":"0","to_nickname":"皇太极","to_user_id":"1060153","user_id":"1060153","video_id":"291"},{"add_time":"1500015185","comment":"评论一番","id":"135","logo":"http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg","nickname":"皇太极","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060153","video_id":"291"}],"comment_times":"2","cover":"http://app.nq6.com/Upload/Cover/1060150/bc5ef57f74cb643236c2c4eaee729e61_50.jpg","cur_frame":"50","desp":"#中国有嘻哈#","file_md5":"bc5ef57f74cb643236c2c4eaee729e61","id":"291","imeil":"7acf482a762201be","is_exec":"1","is_follow":1,"is_hot":"0","is_interest":0,"logo":"http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0","nickname":"相信我","path":"http://app.nq6.com/Upload/Video/bc5ef57f74cb643236c2c4eaee729e61.mp4","play_times":"3","share_times":"0","status":"1","topic_id":"12","type":"3","user_id":"1060150","video_id":"291"},{"add_time":"1499933495","collect_times":"0","comment_list":[],"comment_times":"0","cover":"http://app.nq6.com/Upload/Cover/1060150/bcd142cfa184725ecab19594651edc20_13.jpg","cur_frame":"13","desp":"#中国有嘻哈#","file_md5":"bcd142cfa184725ecab19594651edc20","id":"295","imeil":"7acf482a762201be","is_exec":"1","is_follow":1,"is_hot":"0","is_interest":0,"logo":"http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0","nickname":"相信我","path":"http://app.nq6.com/Upload/Video/bcd142cfa184725ecab19594651edc20.mp4","play_times":"0","share_times":"0","status":"1","topic_id":"12","type":"3","user_id":"1060150","video_id":"295"},{"add_time":"1499933953","collect_times":"0","comment_list":[],"comment_times":"0","cover":"http://app.nq6.com/Upload/Cover/1060153/53c7b5a8d5e79a631b43d21260892ce5_25.jpg","cur_frame":"25","desp":"去了","file_md5":"53c7b5a8d5e79a631b43d21260892ce5","id":"297","imeil":"41633946480a07d9","is_exec":"1","is_follow":0,"is_hot":"0","is_interest":0,"logo":"http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg","nickname":"皇太极","path":"http://app.nq6.com/Upload/Video/53c7b5a8d5e79a631b43d21260892ce5.mp4","play_times":"2","share_times":"0","status":"1","topic_id":"12","type":"3","user_id":"1060153","video_id":"297"},{"add_time":"1499942140","collect_times":"0","comment_list":[],"comment_times":"0","cover":"http://app.nq6.com/Upload/Cover/1060150/aefeadfb5184b3d160963be618d89d15_50.jpg","cur_frame":"50","desp":"#中国有嘻哈#","file_md5":"aefeadfb5184b3d160963be618d89d15","id":"312","imeil":"7acf482a762201be","is_exec":"1","is_follow":1,"is_hot":"0","is_interest":0,"logo":"http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0","nickname":"相信我","path":"http://app.nq6.com/Upload/Video/aefeadfb5184b3d160963be618d89d15.mp4","play_times":"0","share_times":"0","status":"1","topic_id":"12","type":"3","user_id":"1060150","video_id":"312"},{"add_time":"1499945720","collect_times":"0","comment_list":[{"add_time":"1500015235","comment":"是的吗","id":"137","logo":"http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg","nickname":"皇太极","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060153","video_id":"315"}],"comment_times":"1","cover":"http://app.nq6.com/Upload/Cover/1060150/12df48c184dc37e501b93b292851d15f_561.jpg","cur_frame":"561","desp":"#中国有嘻哈#","file_md5":"12df48c184dc37e501b93b292851d15f","id":"315","imeil":"7acf482a762201be","is_exec":"1","is_follow":1,"is_hot":"0","is_interest":0,"logo":"http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0","nickname":"相信我","path":"http://app.nq6.com/Upload/Video/12df48c184dc37e501b93b292851d15f.mp4","play_times":"2","share_times":"0","status":"1","topic_id":"12","type":"3","user_id":"1060150","video_id":"315"}]}
     * msg : 获取视频列表成功
     */

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
         * add_time : 1499932238
         * collect_times : 0
         * comment_list : [{"add_time":"1500015191","comment":"什么","id":"136","logo":"http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg","nickname":"皇太极","status":"0","to_nickname":"皇太极","to_user_id":"1060153","user_id":"1060153","video_id":"291"},{"add_time":"1500015185","comment":"评论一番","id":"135","logo":"http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg","nickname":"皇太极","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060153","video_id":"291"}]
         * comment_times : 2
         * cover : http://app.nq6.com/Upload/Cover/1060150/bc5ef57f74cb643236c2c4eaee729e61_50.jpg
         * cur_frame : 50
         * desp : #中国有嘻哈#
         * file_md5 : bc5ef57f74cb643236c2c4eaee729e61
         * id : 291
         * imeil : 7acf482a762201be
         * is_exec : 1
         * is_follow : 1
         * is_hot : 0
         * is_interest : 0
         * logo : http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0
         * nickname : 相信我
         * path : http://app.nq6.com/Upload/Video/bc5ef57f74cb643236c2c4eaee729e61.mp4
         * play_times : 3
         * share_times : 0
         * status : 1
         * topic_id : 12
         * type : 3
         * user_id : 1060150
         * video_id : 291
         */

        private List<VideoListBean> video_list;

        public List<VideoListBean> getVideo_list() {
            return video_list;
        }

        public void setVideo_list(List<VideoListBean> video_list) {
            this.video_list = video_list;
        }

        public static class VideoListBean implements Serializable{
            private String add_time;
            private String collect_times;
            private String comment_times;
            private String cover;
            private String cur_frame;
            private String desp;
            private String file_md5;
            private String id;
            private String imeil;
            private String is_exec;
            private int is_follow;
            private String is_hot;
            private int is_interest;
            private String logo;
            private String nickname;
            private String path;
            private String play_times;
            private String share_times;
            private String status;
            private String topic_id;
            private String type;
            private String user_id;
            private String video_id;
            private String video_width;
            private String video_height;
            private String download_permiss ;//下载权限
            private String cate;

            public String getCate() {
                return cate;
            }

            public void setCate(String cate) {
                this.cate = cate;
            }

            public String getDownload_permiss() {
                return download_permiss;
            }

            public void setDownload_permiss(String download_permiss) {
                this.download_permiss = download_permiss;
            }

            public String getVideo_width() {
                return video_width;
            }

            public void setVideo_width(String video_width) {
                this.video_width = video_width;
            }

            public String getVideo_height() {
                return video_height;
            }

            public void setVideo_height(String video_height) {
                this.video_height = video_height;
            }

            /**
             * add_time : 1500015191
             * comment : 什么
             * id : 136
             * logo : http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg
             * nickname : 皇太极
             * status : 0
             * to_nickname : 皇太极
             * to_user_id : 1060153
             * user_id : 1060153
             * video_id : 291
             */

            private List<CommentListBean> comment_list;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getCollect_times() {
                return collect_times;
            }

            public void setCollect_times(String collect_times) {
                this.collect_times = collect_times;
            }

            public String getComment_times() {
                return comment_times;
            }

            public void setComment_times(String comment_times) {
                this.comment_times = comment_times;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getCur_frame() {
                return cur_frame;
            }

            public void setCur_frame(String cur_frame) {
                this.cur_frame = cur_frame;
            }

            public String getDesp() {
                return desp;
            }

            public void setDesp(String desp) {
                this.desp = desp;
            }

            public String getFile_md5() {
                return file_md5;
            }

            public void setFile_md5(String file_md5) {
                this.file_md5 = file_md5;
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

            public String getIs_exec() {
                return is_exec;
            }

            public void setIs_exec(String is_exec) {
                this.is_exec = is_exec;
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

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getPlay_times() {
                return play_times;
            }

            public void setPlay_times(String play_times) {
                this.play_times = play_times;
            }

            public String getShare_times() {
                return share_times;
            }

            public void setShare_times(String share_times) {
                this.share_times = share_times;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTopic_id() {
                return topic_id;
            }

            public void setTopic_id(String topic_id) {
                this.topic_id = topic_id;
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

            public List<CommentListBean> getComment_list() {
                return comment_list;
            }

            public void setComment_list(List<CommentListBean> comment_list) {
                this.comment_list = comment_list;
            }

            public static class CommentListBean implements Serializable{
                private String add_time;
                private String comment;
                private String id;
                private String logo;
                private String nickname;
                private String status;
                private String to_nickname;
                private String to_user_id;
                private String user_id;
                private String video_id;

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

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
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

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
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
}

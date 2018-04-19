package com.video.newqu.bean;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/13 16:36
 * 用户个人中心的视频列表
 */
public class AuthorVideoList {

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
        private String count;
        /**
         * user_id : 1060299
         * path : http://192.168.80.110/Upload/video/236850a111a909e9d3205602d4b3212b.mp4
         * size : 664753
         * cover : http://192.168.80.110/Upload/Cover/236850a111a909e9d3205602d4b3212b.jpg
         * times : 0
         * desp :
         * add_time : 1496988495
         * is_hot : 1
         * type : 0
         * comment_times : 2
         * collect_times : 2
         * share_times : 0
         * is_check : 0
         * play_times : 57
         * totals_time : 3.55
         * is_interest : 0
         * comment_list : [{"id":"5","user_id":"1060292","video_id":"217","comment":"再来一条","add_time":"1497336265","is_check":"0","is_del":"0","nickname":"黄天宇","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0"},{"id":"4","user_id":"1060292","video_id":"217","comment":"什么？","add_time":"1497336206","is_check":"0","is_del":"0","nickname":"黄天宇","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0"}]
         * comment_count : 2
         * video_id : 217
         */

        private List<ListsBean> lists;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean {
            private String user_id;
            private String path;
            private String size;
            private String cover;
            private String times;
            private String desp;
            private String add_time;
            private String is_hot;
            private String type;
            private String comment_times;
            private String collect_times;
            private String share_times;
            private String is_check;
            private String play_times;
            private String totals_time;
            private int is_interest;
            private String comment_count;
            private String video_id;
            /**
             * id : 5
             * user_id : 1060292
             * video_id : 217
             * comment : 再来一条
             * add_time : 1497336265
             * is_check : 0
             * is_del : 0
             * nickname : 黄天宇
             * logo : http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0
             */

            private List<CommentListBean> comment_list;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getTimes() {
                return times;
            }

            public void setTimes(String times) {
                this.times = times;
            }

            public String getDesp() {
                return desp;
            }

            public void setDesp(String desp) {
                this.desp = desp;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getIs_hot() {
                return is_hot;
            }

            public void setIs_hot(String is_hot) {
                this.is_hot = is_hot;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getComment_times() {
                return comment_times;
            }

            public void setComment_times(String comment_times) {
                this.comment_times = comment_times;
            }

            public String getCollect_times() {
                return collect_times;
            }

            public void setCollect_times(String collect_times) {
                this.collect_times = collect_times;
            }

            public String getShare_times() {
                return share_times;
            }

            public void setShare_times(String share_times) {
                this.share_times = share_times;
            }

            public String getIs_check() {
                return is_check;
            }

            public void setIs_check(String is_check) {
                this.is_check = is_check;
            }

            public String getPlay_times() {
                return play_times;
            }

            public void setPlay_times(String play_times) {
                this.play_times = play_times;
            }

            public String getTotals_time() {
                return totals_time;
            }

            public void setTotals_time(String totals_time) {
                this.totals_time = totals_time;
            }

            public int getIs_interest() {
                return is_interest;
            }

            public void setIs_interest(int is_interest) {
                this.is_interest = is_interest;
            }

            public String getComment_count() {
                return comment_count;
            }

            public void setComment_count(String comment_count) {
                this.comment_count = comment_count;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setvideo_id(String video_id) {
                this.video_id = video_id;
            }

            public List<CommentListBean> getComment_list() {
                return comment_list;
            }

            public void setComment_list(List<CommentListBean> comment_list) {
                this.comment_list = comment_list;
            }

            public static class CommentListBean {
                private String id;
                private String user_id;
                private String video_id;
                private String comment;
                private String add_time;
                private String is_check;
                private String is_del;
                private String nickname;
                private String logo;

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

                public String getIs_check() {
                    return is_check;
                }

                public void setIs_check(String is_check) {
                    this.is_check = is_check;
                }

                public String getIs_del() {
                    return is_del;
                }

                public void setIs_del(String is_del) {
                    this.is_del = is_del;
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
            }
        }
    }
}

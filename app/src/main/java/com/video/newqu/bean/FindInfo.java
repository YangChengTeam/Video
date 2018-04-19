package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-24 18:21
 */

public class  FindInfo implements Serializable{


    private int code;
    /**
     * city : 鄂州
     * collect_times : 5
     * comment_times : 6
     * fans : 4
     * follows : 3
     * gender : 男
     * goldcoin : 0
     * id : 1060161
     * image_bg : http://app.nq6.com/Public/icon/default_bg.jpg
     * imeil : 7acf482a762201be
     * login_type : 2
     * logo : http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0
     * nickname : 王杰
     * open_id : oGc3u0RmE2dvo1XhzfT1P2fKgGB8
     * password :
     * phone :
     * province : 湖北
     * rank : 1
     * reg_time : 1498112626
     * share_times : 0
     * signature :
     * status : 0
     * video_count : 13
     * video_list : [{"add_time":"1498282897","collect_times":"0","comment_times":"1","cover":"http://app.nq6.com/Upload/Cover/0fe2a7196cad05ad5b470b8e42ce8272.jpg","desp":"jjj","id":"35","is_check":"0","is_hot":"0","path":"http://app.nq6.com/Upload/Video/0fe2a7196cad05ad5b470b8e42ce8272.mp4","play_times":"2","share_times":"0","size":"617587","totals_time":"3.41","type":"0","user_id":"1060161"},{"add_time":"1498282837","collect_times":"0","comment_times":"0","cover":"http://app.nq6.com/Upload/Cover/dbf425d8b7e10b1ba4da1bad64c149ba.jpg","desp":"1111","id":"33","is_check":"0","is_hot":"0","path":"http://app.nq6.com/Upload/Video/dbf425d8b7e10b1ba4da1bad64c149ba.mp4","play_times":"2","share_times":"0","size":"630392","totals_time":"3.25","type":"0","user_id":"1060161"},{"add_time":"1498201058","collect_times":"0","comment_times":"0","cover":"http://app.nq6.com/Upload/Cover/5b0b42598828416389e0ca4a9bbb688d.jpg","desp":"？噢","id":"18","is_check":"0","is_hot":"0","path":"http://app.nq6.com/Upload/Video/5b0b42598828416389e0ca4a9bbb688d.mp4","play_times":"3","share_times":"0","size":"726876","totals_time":"3.91","type":"0","user_id":"1060161"},{"add_time":"1498200721","collect_times":"0","comment_times":"0","cover":"http://app.nq6.com/Upload/Cover/599fd01279496bfd4317d0d1bd52f860.jpg","desp":"键盘","id":"17","is_check":"0","is_hot":"0","path":"http://app.nq6.com/Upload/Video/599fd01279496bfd4317d0d1bd52f860.mp4","play_times":"5","share_times":"0","size":"1433692","totals_time":"0.00","type":"0","user_id":"1060161"}]
     */

    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        private String city;
        private String collect_times;
        private String comment_times;
        private String fans;
        private String follows;
        private String gender;
        private String goldcoin;
        private String id;
        private String image_bg;
        private String imeil;
        private String login_type;
        private String logo;
        private String nickname;
        private String open_id;
        private String password;
        private String phone;
        private String province;
        private String rank;
        private String reg_time;
        private String share_times;
        private String signature;
        private String status;
        private String video_count;
        /**
         * add_time : 1498282897
         * collect_times : 0
         * comment_times : 1
         * cover : http://app.nq6.com/Upload/Cover/0fe2a7196cad05ad5b470b8e42ce8272.jpg
         * desp : jjj
         * id : 35
         * is_check : 0
         * is_hot : 0
         * path : http://app.nq6.com/Upload/Video/0fe2a7196cad05ad5b470b8e42ce8272.mp4
         * play_times : 2
         * share_times : 0
         * size : 617587
         * totals_time : 3.41
         * type : 0
         * user_id : 1060161
         */

        private List<VideoListBean> video_list;

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

        public String getComment_times() {
            return comment_times;
        }

        public void setComment_times(String comment_times) {
            this.comment_times = comment_times;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

        public String getShare_times() {
            return share_times;
        }

        public void setShare_times(String share_times) {
            this.share_times = share_times;
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

        public String getVideo_count() {
            return video_count;
        }

        public void setVideo_count(String video_count) {
            this.video_count = video_count;
        }

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
            private String desp;
            private String id;
            private String is_check;
            private String is_hot;
            private String path;
            private String play_times;
            private String share_times;
            private String size;
            private String totals_time;
            private String type;
            private String user_id;

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

            public String getIs_check() {
                return is_check;
            }

            public void setIs_check(String is_check) {
                this.is_check = is_check;
            }

            public String getIs_hot() {
                return is_hot;
            }

            public void setIs_hot(String is_hot) {
                this.is_hot = is_hot;
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

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getTotals_time() {
                return totals_time;
            }

            public void setTotals_time(String totals_time) {
                this.totals_time = totals_time;
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
        }
    }
}

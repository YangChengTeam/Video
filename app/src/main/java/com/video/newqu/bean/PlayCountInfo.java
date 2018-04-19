package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017/6/5 18:57
 */
public class PlayCountInfo {



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

    public static class DataBean {
        /**
         * video_id : 1
         * platy_times : 20
         */

        private InfoBean info;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            private String video_id;
            private int platy_times;

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            public int getPlaty_times() {
                return platy_times;
            }

            public void setPlaty_times(int platy_times) {
                this.platy_times = platy_times;
            }
        }
    }
}

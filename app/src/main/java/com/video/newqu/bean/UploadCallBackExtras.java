package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/11/24.
 * 上传成功后服务端回调参数
 */

public class UploadCallBackExtras {

    /**
     * Status : 1
     * data : {"request_id":"DB6F06FE-9A64-4087-97F7-F4237D4BD88F","video_id":"7900"}
     * msg : success
     */

    private String Status;
    /**
     * request_id : DB6F06FE-9A64-4087-97F7-F4237D4BD88F
     * video_id : 7900
     */

    private DataBean data;
    private String msg;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
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
        private String request_id;
        private String video_id;

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }
    }
}

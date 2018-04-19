package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017-07-02 13:31
 * 版本更新
 */

public class UpdataApkInfo {


    private int code;
    /**
     * download : http: //app.nq6.com/Upload/Version/2/app-dev-debug.apk
     * update_log : 这是一个描述
     * version : 1.2.1
     * version_code : 1
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

    public static class DataBean {
        private String download;
        private String update_log;
        private String version;
        private String version_code;
        private String size;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getUpdate_log() {
            return update_log;
        }

        public void setUpdate_log(String update_log) {
            this.update_log = update_log;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getVersion_code() {
            return version_code;
        }

        public void setVersion_code(String version_code) {
            this.version_code = version_code;
        }
    }
}

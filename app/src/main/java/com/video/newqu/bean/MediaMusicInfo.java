package com.video.newqu.bean;


import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/12.
 */

public class MediaMusicInfo implements Serializable{

    /**
     * code : 1
     * data : [{"material_id":"13","material_url":"http://video.nq6.com/user-dir/faded.mp3","material_title":"fade","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"14","material_url":"http://video.nq6.com/user-dir/Groove Coverage - God Is a Girl.mp3","material_title":"上帝是个女孩","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"15","material_url":"http://video.nq6.com/user-dir/Immortals.mp3","material_title":"Immortals","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"16","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/5.jpg"},{"material_id":"17","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"18","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/2.jpg"},{"material_id":"19","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/3.jpg"},{"material_id":"20","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"21","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"22","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"23","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/1.jpg"},{"material_id":"24","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/4.jpg"},{"material_id":"25","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/2.jpg"},{"material_id":"26","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/5.jpg"},{"material_id":"27","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/4.jpg"},{"material_id":"28","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/3.jpg"},{"material_id":"29","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/2.jpg"},{"material_id":"30","material_url":"http://video.nq6.com/user-dir/Hotel_California.mp3","material_title":"加利福利亚宾馆","thumb":"http://app.nq6.com/Public/test/1.jpg"}]
     */

    private int code;
    /**
     * material_id : 13
     * material_url : http://video.nq6.com/user-dir/faded.mp3
     * material_title : fade
     * thumb : http://app.nq6.com/Public/test/1.jpg
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
        private String material_id;
        private String material_url;
        private String material_title;
        private String thumb;
        private boolean isDownloading;

        public boolean isSelector() {
            return isSelector;
        }

        public void setSelector(boolean selector) {
            isSelector = selector;
        }

        private boolean isSelector;

        public boolean isDownloading() {
            return isDownloading;
        }

        public void setDownloading(boolean downloading) {
            isDownloading = downloading;
        }

        public String getMaterial_id() {
            return material_id;
        }

        public void setMaterial_id(String material_id) {
            this.material_id = material_id;
        }

        public String getMaterial_url() {
            return material_url;
        }

        public void setMaterial_url(String material_url) {
            this.material_url = material_url;
        }

        public String getMaterial_title() {
            return material_title;
        }

        public void setMaterial_title(String material_title) {
            this.material_title = material_title;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}

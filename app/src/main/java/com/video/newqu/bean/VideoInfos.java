package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/8/26
 */

public class VideoInfos {

    private String video_width;
    private String video_height;
    private String video_bitrate;
    private String video_durtion;
    private String video_fram;
    private String video_rotation;

    public String getVideo_rotation() {
        return video_rotation;
    }

    public void setVideo_rotation(String video_rotation) {
        this.video_rotation = video_rotation;
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

    public String getVideo_bitrate() {
        return video_bitrate;
    }

    public void setVideo_bitrate(String video_bitrate) {
        this.video_bitrate = video_bitrate;
    }

    public String getVideo_durtion() {
        return video_durtion;
    }

    public void setVideo_durtion(String video_durtion) {
        this.video_durtion = video_durtion;
    }

    public String getVideo_fram() {
        return video_fram;
    }

    public void setVideo_fram(String video_fram) {
        this.video_fram = video_fram;
    }

    @Override
    public String toString() {
        return "VideoInfos{" +
                "video_width='" + video_width + '\'' +
                ", video_height='" + video_height + '\'' +
                ", video_bitrate='" + video_bitrate + '\'' +
                ", video_durtion='" + video_durtion + '\'' +
                ", video_fram='" + video_fram + '\'' +
                ", video_rotation='" + video_rotation + '\'' +
                '}';
    }
}

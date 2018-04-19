package com.video.newqu.event;

/**
 * TinyHung@outlook.com
 * 2017/5/24 15:52
 * 上传事件
 */
public class UploadMessageEvent {

    private int state;
    private int progress;
    private String videoPath;
    private String resouceVideoPath;
    private long videoCover;
    private String videoID;


    public String getResouceVideoPath() {
        return resouceVideoPath;
    }

    public void setResouceVideoPath(String resouceVideoPath) {
        this.resouceVideoPath = resouceVideoPath;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public long getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(long videoCover) {
        this.videoCover = videoCover;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
}

package com.video.newqu.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * TinyHung@Outlook.com
 * 2018/1/4.
 * 用户的播放行为记录表
 */
@Entity
public class UserVideoPlayerList {
    @Id(autoincrement = true)
    private Long ID;
    private String userID;
    private String emilID;
    private long addTime;
    private String videoID;
    private String videoTag;
    private int state;
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public String getVideoTag() {
        return this.videoTag;
    }
    public void setVideoTag(String videoTag) {
        this.videoTag = videoTag;
    }
    public String getVideoID() {
        return this.videoID;
    }
    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
    public long getAddTime() {
        return this.addTime;
    }
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
    public String getEmilID() {
        return this.emilID;
    }
    public void setEmilID(String emilID) {
        this.emilID = emilID;
    }
    public String getUserID() {
        return this.userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 1217346300)
    public UserVideoPlayerList(Long ID, String userID, String emilID, long addTime,
            String videoID, String videoTag, int state) {
        this.ID = ID;
        this.userID = userID;
        this.emilID = emilID;
        this.addTime = addTime;
        this.videoID = videoID;
        this.videoTag = videoTag;
        this.state = state;
    }
    @Generated(hash = 1880610867)
    public UserVideoPlayerList() {
    }
}

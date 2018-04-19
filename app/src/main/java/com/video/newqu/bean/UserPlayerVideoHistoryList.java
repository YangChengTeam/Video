package com.video.newqu.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * TinyHung@Outlook.com
 * 2017/10/19.
 * 用户播放的历史记录
 */
@Entity
public class UserPlayerVideoHistoryList implements Parcelable,Comparable<UserPlayerVideoHistoryList>{

    @Id(autoincrement = true)
    private Long ID;
    private int id;
    private String userName;
    private String userSinger;
    private String userCover;
    private String videoDesp;
    private String videoLikeCount;
    private String videoCommendCount;
    private String videoShareCount;
    private String userId;
    private String videoId;
    private String videoCover;
    private boolean isSelector;
    private String uploadTime;
    private long addTime;
    private int is_interest;
    private int is_follow;
    private String videoPath;
    private String videoPlayerCount;
    private String videoType;
    private String downloadPermiss;
    private String status;
    private int itemIndex;

    protected UserPlayerVideoHistoryList(Parcel in) {
        if (in.readByte() == 0) {
            ID = null;
        } else {
            ID = in.readLong();
        }
        id = in.readInt();
        userName = in.readString();
        userSinger = in.readString();
        userCover = in.readString();
        videoDesp = in.readString();
        videoLikeCount = in.readString();
        videoCommendCount = in.readString();
        videoShareCount = in.readString();
        userId = in.readString();
        videoId = in.readString();
        videoCover = in.readString();
        isSelector = in.readByte() != 0;
        uploadTime = in.readString();
        addTime = in.readLong();
        is_interest = in.readInt();
        is_follow = in.readInt();
        videoPath = in.readString();
        videoPlayerCount = in.readString();
        videoType = in.readString();
        downloadPermiss = in.readString();
        status = in.readString();
    }

    public static final Creator<UserPlayerVideoHistoryList> CREATOR = new Creator<UserPlayerVideoHistoryList>() {
        @Override
        public UserPlayerVideoHistoryList createFromParcel(Parcel in) {
            return new UserPlayerVideoHistoryList(in);
        }

        @Override
        public UserPlayerVideoHistoryList[] newArray(int size) {
            return new UserPlayerVideoHistoryList[size];
        }
    };

    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDownloadPermiss() {
        return this.downloadPermiss;
    }
    public void setDownloadPermiss(String downloadPermiss) {
        this.downloadPermiss = downloadPermiss;
    }
    public String getVideoType() {
        return this.videoType;
    }
    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }
    public String getVideoPlayerCount() {
        return this.videoPlayerCount;
    }
    public void setVideoPlayerCount(String videoPlayerCount) {
        this.videoPlayerCount = videoPlayerCount;
    }
    public String getVideoPath() {
        return this.videoPath;
    }
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    public int getIs_follow() {
        return this.is_follow;
    }
    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }
    public int getIs_interest() {
        return this.is_interest;
    }
    public void setIs_interest(int is_interest) {
        this.is_interest = is_interest;
    }
    public long getAddTime() {
        return this.addTime;
    }
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
    public String getUploadTime() {
        return this.uploadTime;
    }
    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
    public boolean getIsSelector() {
        return this.isSelector;
    }
    public void setIsSelector(boolean isSelector) {
        this.isSelector = isSelector;
    }
    public String getVideoCover() {
        return this.videoCover;
    }
    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }
    public String getVideoId() {
        return this.videoId;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getVideoShareCount() {
        return this.videoShareCount;
    }
    public void setVideoShareCount(String videoShareCount) {
        this.videoShareCount = videoShareCount;
    }
    public String getVideoCommendCount() {
        return this.videoCommendCount;
    }
    public void setVideoCommendCount(String videoCommendCount) {
        this.videoCommendCount = videoCommendCount;
    }
    public String getVideoLikeCount() {
        return this.videoLikeCount;
    }
    public void setVideoLikeCount(String videoLikeCount) {
        this.videoLikeCount = videoLikeCount;
    }
    public String getVideoDesp() {
        return this.videoDesp;
    }
    public void setVideoDesp(String videoDesp) {
        this.videoDesp = videoDesp;
    }
    public String getUserCover() {
        return this.userCover;
    }
    public void setUserCover(String userCover) {
        this.userCover = userCover;
    }
    public String getUserSinger() {
        return this.userSinger;
    }
    public void setUserSinger(String userSinger) {
        this.userSinger = userSinger;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 302345647)
    public UserPlayerVideoHistoryList(Long ID, int id, String userName, String userSinger, String userCover,
            String videoDesp, String videoLikeCount, String videoCommendCount, String videoShareCount,
            String userId, String videoId, String videoCover, boolean isSelector, String uploadTime,
            long addTime, int is_interest, int is_follow, String videoPath, String videoPlayerCount,
            String videoType, String downloadPermiss, String status, int itemIndex) {
        this.ID = ID;
        this.id = id;
        this.userName = userName;
        this.userSinger = userSinger;
        this.userCover = userCover;
        this.videoDesp = videoDesp;
        this.videoLikeCount = videoLikeCount;
        this.videoCommendCount = videoCommendCount;
        this.videoShareCount = videoShareCount;
        this.userId = userId;
        this.videoId = videoId;
        this.videoCover = videoCover;
        this.isSelector = isSelector;
        this.uploadTime = uploadTime;
        this.addTime = addTime;
        this.is_interest = is_interest;
        this.is_follow = is_follow;
        this.videoPath = videoPath;
        this.videoPlayerCount = videoPlayerCount;
        this.videoType = videoType;
        this.downloadPermiss = downloadPermiss;
        this.status = status;
        this.itemIndex = itemIndex;
    }
    @Generated(hash = 1017991234)
    public UserPlayerVideoHistoryList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (ID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(ID);
        }
        dest.writeInt(id);
        dest.writeString(userName);
        dest.writeString(userSinger);
        dest.writeString(userCover);
        dest.writeString(videoDesp);
        dest.writeString(videoLikeCount);
        dest.writeString(videoCommendCount);
        dest.writeString(videoShareCount);
        dest.writeString(userId);
        dest.writeString(videoId);
        dest.writeString(videoCover);
        dest.writeByte((byte) (isSelector ? 1 : 0));
        dest.writeString(uploadTime);
        dest.writeLong(addTime);
        dest.writeInt(is_interest);
        dest.writeInt(is_follow);
        dest.writeString(videoPath);
        dest.writeString(videoPlayerCount);
        dest.writeString(videoType);
        dest.writeString(downloadPermiss);
        dest.writeString(status);
    }

    @Override
    public int compareTo(@NonNull UserPlayerVideoHistoryList o) {
        return o.getAddTime()>this.getAddTime()?1:0;
    }
    public int getItemIndex() {
        return this.itemIndex;
    }
    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }
}


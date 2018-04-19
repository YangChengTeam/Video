package com.video.newqu.bean;

import com.video.newqu.comadapter.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-05-22 21:34
 * 测试的视频列表
 */

public class VideoListInfo implements MultiItemEntity,Serializable {

    public static final int ITEM_0=0;
    public static final int ITEM_1=1;

    private int videoId;
    private String videoTitle;
    private String videoImage;
    private String videoUrl;
    private int itemType;
    private String authorName;
    private String authorImage;
    private long videoPublishTime;
    private long playNum;
    private int followNum;
    private int comentNum;
    private int shareNum;
    private boolean isFollow;

    List<ComentContent> mComentContentList;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public long getVideoPublishTime() {
        return videoPublishTime;
    }

    public void setVideoPublishTime(long videoPublishTime) {
        this.videoPublishTime = videoPublishTime;
    }

    public long getPlayNum() {
        return playNum;
    }

    public void setPlayNum(long playNum) {
        this.playNum = playNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public int getComentNum() {
        return comentNum;
    }

    public void setComentNum(int comentNum) {
        this.comentNum = comentNum;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public List<ComentContent> getComentContentList() {
        return mComentContentList;
    }

    public void setComentContentList(List<ComentContent> comentContentList) {
        mComentContentList = comentContentList;
    }

    /**
     * 评论内容
     */
    public static class ComentContent implements Serializable{

        private String commentName;
        private String commentContent;

        public String getCommentName() {
            return commentName;
        }

        public void setCommentName(String commentName) {
            this.commentName = commentName;
        }

        public String getCommentContent() {
            return commentContent;
        }

        public void setCommentContent(String commentContent) {
            this.commentContent = commentContent;
        }
    }
}

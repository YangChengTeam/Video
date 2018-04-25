package com.video.newqu.bean;

import android.support.annotation.NonNull;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import java.io.Serializable;

/**
 * YuyeTinyHung@outlook.com
 * 2017/7/9
 * 视频合并与视频上传Object
 */

@Entity
public class UploadVideoInfo  implements Comparable<UploadVideoInfo> ,Serializable {

    @Id(autoincrement = true)
    private Long id;
    private int uploadProgress;//本地上传进度
    private long videoCoverFps;//封面(毫秒)
    private float videoFps;//视频帧率
    private int uploadType;
    private boolean isPrivate;
    private int videoWidth;
    private int videoHeight;
    private int videoBitrate;//码率
    private int videoDurtion;//时长
    private int sourceType;
    private int itemType;//类型
    private int composeState;//合成的消息类型
    private int composeProgress;//合成的进度
    private String videoName;
    private String videoFileKey;
    private String filePath;
    private String videoDesp;
    private String uploadID;
    private String musicPath;
    private String musicID;
    private String compostOutFilePath;//输出路径
    private String resoucePath;//源路径，合并封面图展示用到
    private String serviceCallBackBody;//上传成功后服务端返回的回调信息
    private String serviceVideoId;//服务端生成的最终ID
    private String downloadPermiss;

    public String getServiceVideoId() {
        return this.serviceVideoId;
    }
    public void setServiceVideoId(String serviceVideoId) {
        this.serviceVideoId = serviceVideoId;
    }
    public String getServiceCallBackBody() {
        return this.serviceCallBackBody;
    }
    public void setServiceCallBackBody(String serviceCallBackBody) {
        this.serviceCallBackBody = serviceCallBackBody;
    }
    public String getResoucePath() {
        return this.resoucePath;
    }
    public void setResoucePath(String resoucePath) {
        this.resoucePath = resoucePath;
    }
    public String getCompostOutFilePath() {
        return this.compostOutFilePath;
    }
    public void setCompostOutFilePath(String compostOutFilePath) {
        this.compostOutFilePath = compostOutFilePath;
    }
    public String getMusicID() {
        return this.musicID;
    }
    public void setMusicID(String musicID) {
        this.musicID = musicID;
    }
    public String getMusicPath() {
        return this.musicPath;
    }
    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }
    public String getUploadID() {
        return this.uploadID;
    }
    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }
    public String getVideoDesp() {
        return this.videoDesp;
    }
    public void setVideoDesp(String videoDesp) {
        this.videoDesp = videoDesp;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getVideoFileKey() {
        return this.videoFileKey;
    }
    public void setVideoFileKey(String videoFileKey) {
        this.videoFileKey = videoFileKey;
    }
    public String getVideoName() {
        return this.videoName;
    }
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
    public int getComposeProgress() {
        return this.composeProgress;
    }
    public void setComposeProgress(int composeProgress) {
        this.composeProgress = composeProgress;
    }
    public int getComposeState() {
        return this.composeState;
    }
    public void setComposeState(int composeState) {
        this.composeState = composeState;
    }
    public int getItemType() {
        return this.itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
    public int getSourceType() {
        return this.sourceType;
    }
    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }
    public int getVideoDurtion() {
        return this.videoDurtion;
    }
    public void setVideoDurtion(int videoDurtion) {
        this.videoDurtion = videoDurtion;
    }
    public int getVideoBitrate() {
        return this.videoBitrate;
    }
    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }
    public int getVideoHeight() {
        return this.videoHeight;
    }
    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }
    public int getVideoWidth() {
        return this.videoWidth;
    }
    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }
    public boolean getIsPrivate() {
        return this.isPrivate;
    }
    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    public int getUploadType() {
        return this.uploadType;
    }
    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }
    public float getVideoFps() {
        return this.videoFps;
    }
    public void setVideoFps(float videoFps) {
        this.videoFps = videoFps;
    }
    public long getVideoCoverFps() {
        return this.videoCoverFps;
    }
    public void setVideoCoverFps(long videoCoverFps) {
        this.videoCoverFps = videoCoverFps;
    }
    public int getUploadProgress() {
        return this.uploadProgress;
    }
    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 143164928)
    public UploadVideoInfo(Long id, int uploadProgress, long videoCoverFps,
            float videoFps, int uploadType, boolean isPrivate, int videoWidth,
            int videoHeight, int videoBitrate, int videoDurtion, int sourceType,
            int itemType, int composeState, int composeProgress, String videoName,
            String videoFileKey, String filePath, String videoDesp,
            String uploadID, String musicPath, String musicID,
            String compostOutFilePath, String resoucePath,
            String serviceCallBackBody, String serviceVideoId,
            String downloadPermiss) {
        this.id = id;
        this.uploadProgress = uploadProgress;
        this.videoCoverFps = videoCoverFps;
        this.videoFps = videoFps;
        this.uploadType = uploadType;
        this.isPrivate = isPrivate;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoBitrate = videoBitrate;
        this.videoDurtion = videoDurtion;
        this.sourceType = sourceType;
        this.itemType = itemType;
        this.composeState = composeState;
        this.composeProgress = composeProgress;
        this.videoName = videoName;
        this.videoFileKey = videoFileKey;
        this.filePath = filePath;
        this.videoDesp = videoDesp;
        this.uploadID = uploadID;
        this.musicPath = musicPath;
        this.musicID = musicID;
        this.compostOutFilePath = compostOutFilePath;
        this.resoucePath = resoucePath;
        this.serviceCallBackBody = serviceCallBackBody;
        this.serviceVideoId = serviceVideoId;
        this.downloadPermiss = downloadPermiss;
    }
    @Generated(hash = 1453632770)
    public UploadVideoInfo() {
    }


    @Override
    public int compareTo(@NonNull UploadVideoInfo o) {
        return itemType>o.getItemType()?1:0;
    }
    public String getDownloadPermiss() {
        return this.downloadPermiss;
    }
    public void setDownloadPermiss(String downloadPermiss) {
        this.downloadPermiss = downloadPermiss;
    }
}

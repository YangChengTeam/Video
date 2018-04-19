package com.video.newqu.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * TinyHung@outlook.com
 * 2017/7/15 11:39
 */
@Entity
public class WeiChactVideoInfo {

    @Id(autoincrement = true)
    private Long ID;
    private String filePath;
    private boolean isUploadFinlish;
    private String fileKey;
    private String fileName;
    private String video_width;
    private String video_height;
    private String frame_num;
    private String code_rate;
    private String video_durtion;
    private int sourceType;
    private String uploadID;
    public String getUploadID() {
        return this.uploadID;
    }
    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }
    public int getSourceType() {
        return this.sourceType;
    }
    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }
    public String getVideo_durtion() {
        return this.video_durtion;
    }
    public void setVideo_durtion(String video_durtion) {
        this.video_durtion = video_durtion;
    }
    public String getCode_rate() {
        return this.code_rate;
    }
    public void setCode_rate(String code_rate) {
        this.code_rate = code_rate;
    }
    public String getFrame_num() {
        return this.frame_num;
    }
    public void setFrame_num(String frame_num) {
        this.frame_num = frame_num;
    }
    public String getVideo_height() {
        return this.video_height;
    }
    public void setVideo_height(String video_height) {
        this.video_height = video_height;
    }
    public String getVideo_width() {
        return this.video_width;
    }
    public void setVideo_width(String video_width) {
        this.video_width = video_width;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileKey() {
        return this.fileKey;
    }
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
    public boolean getIsUploadFinlish() {
        return this.isUploadFinlish;
    }
    public void setIsUploadFinlish(boolean isUploadFinlish) {
        this.isUploadFinlish = isUploadFinlish;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 295107291)
    public WeiChactVideoInfo(Long ID, String filePath, boolean isUploadFinlish,
            String fileKey, String fileName, String video_width,
            String video_height, String frame_num, String code_rate,
            String video_durtion, int sourceType, String uploadID) {
        this.ID = ID;
        this.filePath = filePath;
        this.isUploadFinlish = isUploadFinlish;
        this.fileKey = fileKey;
        this.fileName = fileName;
        this.video_width = video_width;
        this.video_height = video_height;
        this.frame_num = frame_num;
        this.code_rate = code_rate;
        this.video_durtion = video_durtion;
        this.sourceType = sourceType;
        this.uploadID = uploadID;
    }
    @Generated(hash = 1942760779)
    public WeiChactVideoInfo() {
    }

}

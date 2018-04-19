package com.video.newqu.bean;

import android.support.annotation.NonNull;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * $TinyHung@Outlook.com
 * 2017/8/3
 * 扫描本地微信视频
 */
@Entity
public class WeiXinVideo  implements Comparable,Serializable{

    @Id(autoincrement = true)
    private Long ID;
    private String fileName;
    private String videoPath;
    private Long videoCreazeTime;
    private int videoDortion;
    private boolean isSelector;
    private String videpThbunPath;
    private String fileKey;
    public String getFileKey() {
        return this.fileKey;
    }
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
    public String getVidepThbunPath() {
        return this.videpThbunPath;
    }
    public void setVidepThbunPath(String videpThbunPath) {
        this.videpThbunPath = videpThbunPath;
    }
    public boolean getIsSelector() {
        return this.isSelector;
    }
    public void setIsSelector(boolean isSelector) {
        this.isSelector = isSelector;
    }
    public int getVideoDortion() {
        return this.videoDortion;
    }
    public void setVideoDortion(int videoDortion) {
        this.videoDortion = videoDortion;
    }
    public Long getVideoCreazeTime() {
        return this.videoCreazeTime;
    }
    public void setVideoCreazeTime(Long videoCreazeTime) {
        this.videoCreazeTime = videoCreazeTime;
    }
    public String getVideoPath() {
        return this.videoPath;
    }
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 1956330026)
    public WeiXinVideo(Long ID, String fileName, String videoPath,
            Long videoCreazeTime, int videoDortion, boolean isSelector,
            String videpThbunPath, String fileKey) {
        this.ID = ID;
        this.fileName = fileName;
        this.videoPath = videoPath;
        this.videoCreazeTime = videoCreazeTime;
        this.videoDortion = videoDortion;
        this.isSelector = isSelector;
        this.videpThbunPath = videpThbunPath;
        this.fileKey = fileKey;
    }
    @Generated(hash = 415560093)
    public WeiXinVideo() {
    }

    @Override
    public int compareTo(@NonNull Object o) {
        WeiXinVideo weiXinVideo= (WeiXinVideo) o;
        return videoCreazeTime>weiXinVideo.getVideoCreazeTime()?1:0;
    }
}

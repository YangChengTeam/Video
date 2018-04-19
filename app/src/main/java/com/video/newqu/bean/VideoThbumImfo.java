package com.video.newqu.bean;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/6/23 17:47
 */

public class VideoThbumImfo implements Serializable{

    private String videoframePath;//帧地址
    private String secondSum;//秒
    private int frameSum=-1;//帧数
    private boolean isSelector; //是否选中


    public String getVideoframePath() {
        return videoframePath;
    }

    public void setVideoframePath(String videoframePath) {
        this.videoframePath = videoframePath;
    }

    public String getSecondSum() {
        return secondSum;
    }

    public void setSecondSum(String secondSum) {
        this.secondSum = secondSum;
    }

    public int getFrameSum() {
        return frameSum;
    }

    public void setFrameSum(int frameSum) {
        this.frameSum = frameSum;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }
}

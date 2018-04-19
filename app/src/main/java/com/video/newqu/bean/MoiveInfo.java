package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017/7/3 12:59
 * 影片信息
 */
public class MoiveInfo {

    /**
     * ver : 1
     * errcode : 0
     * vstreamcnt : 1
     * astreamcnt : 1
     * streamcnt : 2
     * format : mov,mp4,m4a,3gp,3g2,mj2
     * file : /storage/emulated/0/测试/47010153805B6AF25CC1824D83EEBBDE.mp4
     * duration : 203.5
     * starttime : 0.0
     * bitrate : 1102
     * samplerate : 44100
     * channels : 2
     * fps : 29.0
     * pixfmt : yuv420p
     * samplefmt : fltp
     * vcodec : h264
     * acodec : aac
     * width : 960
     * height : 540
     * vprofile : Main
     * vcodectag : avc1
     * acodectag : mp4a
     */

    private int ver;
    private int errcode;
    private int vstreamcnt;
    private int astreamcnt;
    private int streamcnt;
    private String format;
    private String file;
    private double duration;
    private double starttime;
    private int bitrate;
    private int samplerate;
    private int channels;
    private double fps;
    private String pixfmt;
    private String samplefmt;
    private String vcodec;
    private String acodec;
    private int width;
    private int height;
    private String vprofile;
    private String vcodectag;
    private String acodectag;

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public int getVstreamcnt() {
        return vstreamcnt;
    }

    public void setVstreamcnt(int vstreamcnt) {
        this.vstreamcnt = vstreamcnt;
    }

    public int getAstreamcnt() {
        return astreamcnt;
    }

    public void setAstreamcnt(int astreamcnt) {
        this.astreamcnt = astreamcnt;
    }

    public int getStreamcnt() {
        return streamcnt;
    }

    public void setStreamcnt(int streamcnt) {
        this.streamcnt = streamcnt;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getStarttime() {
        return starttime;
    }

    public void setStarttime(double starttime) {
        this.starttime = starttime;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public String getPixfmt() {
        return pixfmt;
    }

    public void setPixfmt(String pixfmt) {
        this.pixfmt = pixfmt;
    }

    public String getSamplefmt() {
        return samplefmt;
    }

    public void setSamplefmt(String samplefmt) {
        this.samplefmt = samplefmt;
    }

    public String getVcodec() {
        return vcodec;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }

    public String getAcodec() {
        return acodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getVprofile() {
        return vprofile;
    }

    public void setVprofile(String vprofile) {
        this.vprofile = vprofile;
    }

    public String getVcodectag() {
        return vcodectag;
    }

    public void setVcodectag(String vcodectag) {
        this.vcodectag = vcodectag;
    }

    public String getAcodectag() {
        return acodectag;
    }

    public void setAcodectag(String acodectag) {
        this.acodectag = acodectag;
    }
}

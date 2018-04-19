package com.video.newqu.contants;

import com.video.newqu.util.SharedPreferencesUtil;
/**
 * TinyHung@outlook.com
 * 2017/5/26 14:09
 * 设置中心配置
 */
public class ConfigSet {

    public static final boolean IS_DEBUG=true;
    public static final int SAVE_USER_LOOK_VIDEO_HISTORY_COUNT = 200;//配置用户观看视频记录的最大存储长度
    private static ConfigSet mConfigSet;
    private boolean mobileUpload;//移动网络上传视频
    private boolean mobilePlayer;//移动网络播放视频
    private boolean wifiAuthPlayer;//WIFI网络自动播放
    private boolean palyerLoop;//循环播放
    private boolean isAddWatermark;//是否自动添加水印
    private boolean playerModel;//是否始终以全屏列表模式播放视频
    private boolean isSaveVideo;//是否自动保存编辑或者录制的视频到本地
    private boolean isWifiTips;//是否提示过流量下播放视频

    public static  synchronized ConfigSet getInstance(){
        synchronized (ConfigSet.class){
            if(null==mConfigSet){
                mConfigSet=new ConfigSet();
            }
        }
        return mConfigSet;
    }


    public boolean isShowAutoComment() {
        return SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_PLAYER_VIDEO_ISSHOW_AUTOCOMMENT,true);
    }

    public void setShowAutoComment() {
        boolean aBoolean = SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_PLAYER_VIDEO_ISSHOW_AUTOCOMMENT, true);
        aBoolean=!aBoolean;
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_PLAYER_VIDEO_ISSHOW_AUTOCOMMENT,aBoolean);
    }


    public boolean isWifiTips() {
        return isWifiTips;
    }
    public void setWifiTips(boolean wifiTips) {
        isWifiTips = wifiTips;
    }


    public boolean isMobileUpload() {
        return mobileUpload;
    }

    public void setMobileUpload(boolean mobileUpload) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_MOBILE_UPLOAD,mobileUpload);
        this.mobileUpload = mobileUpload;
    }

    public boolean isMobilePlayer() {
        return mobilePlayer;
    }

    public void setMobilePlayer(boolean mobilePlayer) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_MOBILE_PLAYER,mobilePlayer);
        this.mobilePlayer = mobilePlayer;
    }

    public boolean isWifiAuthPlayer() {
        return wifiAuthPlayer;
    }

    public void setWifiAuthPlayer(boolean wifiAuthPlayer) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_WIFI_AUTH_PLAYER,wifiAuthPlayer);
        this.wifiAuthPlayer = wifiAuthPlayer;
    }

    public boolean isPalyerLoop() {
        return palyerLoop;
    }

    public void setPalyerLoop(boolean palyerLoop) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_VIDEO_PLAYER_LOOP,palyerLoop);
        this.palyerLoop = palyerLoop;
    }



    public boolean isAddWatermark() {
        return isAddWatermark;
    }

    public void setAddWatermark(boolean addWatermark) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_VIDEO_WATERMARK,addWatermark);
        isAddWatermark = addWatermark;
    }



    public boolean isSaveVideo() {
        return isSaveVideo;
    }

    public void setSaveVideo(boolean saveVideo) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_VIDEO_SAVE_VIDEO,saveVideo);
        isSaveVideo = saveVideo;
    }


    public boolean isPlayerModel() {
        return playerModel;
    }

    public void setPlayerModel(boolean playerModel) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_VIDEO_PLAYER_MOLDER,playerModel);
        this.playerModel = playerModel;
    }


    /**
     * 初始设置状态
     */
    public void init() {
        this.mobileUpload=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_MOBILE_UPLOAD);
        this.mobilePlayer=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_MOBILE_PLAYER);
        this.palyerLoop=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_VIDEO_PLAYER_LOOP);
        this.isAddWatermark=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_VIDEO_WATERMARK);
        this.isSaveVideo=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_VIDEO_SAVE_VIDEO);
        this.playerModel=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_VIDEO_PLAYER_MOLDER);
        this.wifiAuthPlayer=SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_WIFI_AUTH_PLAYER);
    }

    /**
     * 第一次配置
     */
    public void initSetting() {
        setWifiAuthPlayer(true);
        setPalyerLoop(true);
        setAddWatermark(true);
        setSaveVideo(false);
        setPlayerModel(true);
    }
}

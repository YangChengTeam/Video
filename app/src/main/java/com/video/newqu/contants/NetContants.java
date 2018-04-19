package com.video.newqu.contants;

import android.os.Environment;

import com.video.newqu.manager.ApplicationManager;

import java.io.File;

/**
 * TinyHung@outlook.com
 * 2017/3/18 9:59
 * 所有和网络相关的常量
 */
public interface NetContants {


    /**
     * 网络状态
     */
    int NETWORK_STATE_ERROR = -1;             // 网络状态获取异常
    int NETWORK_STATE_NO_CONNECTION = 0;      // 网络状态当前没有连接
    int NETWORK_STATE_WIFI = 1;               // 当前使用的是wifi
    int NETWORK_STATE_3G = 2;                 // 当前使用的是3g


    String BASE_HOST="http://app.nq6.com/api/index/";
    String BASE_IP="http://app.nq6.com/";
    String BASE_VIDEO_HOST="http://app.nq6.com/api/video/";
    String MEDIA_EDIT_HOST="http://sc.wk2.com/Api/Appnq6/";

    /**
     * 缓存目录
     */
    String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/newqu/cache/";

    /**
     * 微信视频文件目录
     */
    String WEICHAT_VIDEO_PATH = Environment.getExternalStorageDirectory()+"/tencent/MicroMsg";


    String SD_DIR = ApplicationManager.getInstance().getSdPath();

    String BASE_SD_DIR = SD_DIR + File.separator + "newqu";

    String BASE_CACHE_PATH = BASE_SD_DIR + File.separator + "cache";

    int NOTIFACATION_ID_UPDATA = 0;
    int NOTIFACATION_ID_TASK = 1;
    int NOTIFACATION_ID_INSTALL = 2;
}

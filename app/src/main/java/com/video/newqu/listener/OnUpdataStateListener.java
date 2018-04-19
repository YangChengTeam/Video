package com.video.newqu.listener;
import com.video.newqu.bean.UpdataApkInfo;

import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2017/8/14.
 */

public interface OnUpdataStateListener {
    void onNeedUpdata(UpdataApkInfo updataApkInfo);
    void onNotUpdata(String data);
    void onUpdataError(String data);
}

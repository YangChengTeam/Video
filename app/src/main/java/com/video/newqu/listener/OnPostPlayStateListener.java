package com.video.newqu.listener;

/**
 * TinyHung@outlook.com
 * 2017/7/8 11:07
 * 统计播放记录监听器
 */
public interface OnPostPlayStateListener {
    void onPostPlayStateComple(String newPlayCount);
    void onPostPlayStateError();
}

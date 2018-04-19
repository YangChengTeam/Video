package com.video.newqu.listener;

import com.video.newqu.bean.UserPlayerVideoHistoryList;

/**
 * TinyHung@Outlook.com
 * 2017/10/19.
 * 用户观看视频历史记录列表点击事件
 */

public interface OnUserPlayerHistoryClickListener {
    void onUserIcon(String userID);
    void onDeleteVideo(UserPlayerVideoHistoryList data,int poistion);
    void onItemClick(int poistion);
}

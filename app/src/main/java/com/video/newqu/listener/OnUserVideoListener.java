package com.video.newqu.listener;

/**
 * TinyHung@Outlook.com
 * 2017/10/17
 */

public interface OnUserVideoListener {
    void onItemClick(int poistion);
    void onLongClick(String videoID);
    void onDeleteVideo(String videoID);
    void onPublicVideo(String videoID);
    void onUnFollowVideo(String videoID);
    void onHeaderIcon(String userID);
}

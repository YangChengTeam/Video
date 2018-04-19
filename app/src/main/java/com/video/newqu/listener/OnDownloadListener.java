package com.video.newqu.listener;

/**
 * TinyHung@Outlook.com
 * 2017/8/31.
 */

public interface OnDownloadListener {
    void onDownloading();
    void onDownloadFinlish();
    void onDownloadError(String error);
}

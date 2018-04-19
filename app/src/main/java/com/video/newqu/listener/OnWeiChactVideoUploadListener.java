package com.video.newqu.listener;


/**
 * TinyHung@outlook.com
 * 2017/7/10 18:03
 */
public interface OnWeiChactVideoUploadListener {

    void onUploadNoFinlish();
    void onUploadProgress(String fileName, int progress);
    void onUploadFnlish(String fielName);
    void onUploadFai(String fielName);
    void onUploadStart(String fileName);
}

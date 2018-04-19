package com.video.newqu.listener;

import com.video.newqu.bean.UploadVideoInfo;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/10 18:03
 */
public interface OnVideoUploadListener {
    void onUploadNoFinlish();
    void onUploadProgress(Long videoID,int progress);
    void onUploadFnlish(Long videoID);
    void onUploadFai(Long videoID);
    void onUploadStart(Long videoID);
    void onUnloadingList(List<UploadVideoInfo> uploadVideoInfos);
}

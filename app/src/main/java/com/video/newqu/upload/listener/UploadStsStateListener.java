package com.video.newqu.upload.listener;

import com.video.newqu.bean.UploadVideoInfo;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件上传监听
 */

public interface UploadStsStateListener {
    void uploadStart(UploadVideoInfo data);
    void uploadSuccess(UploadVideoInfo data);
    void uploadProgress(UploadVideoInfo data,int progress);
    void uploadFail( UploadVideoInfo data,boolean isCanelTask,String msg);
    void uploadSynch(UploadVideoInfo videoInfo);
}

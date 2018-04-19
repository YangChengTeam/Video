package com.video.newqu.upload.listener;

import com.video.newqu.bean.UploadVideoInfo;


/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件上传监听
 */

public interface VideoUploadListener {
    void uploadStart(UploadVideoInfo data);
    void uploadSuccess(UploadVideoInfo data,String extras);
    void uploadProgress(UploadVideoInfo data);
    void uploadFail(UploadVideoInfo data, int stateCode,int errorCode,String errorMsg);
}

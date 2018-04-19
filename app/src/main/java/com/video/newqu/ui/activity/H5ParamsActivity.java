package com.video.newqu.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.video.newqu.util.ToastUtils;

/**
 * TinyHung@Outlook.com
 * 2017/12/1.
 * 接收网页参数跳转至视频播放详情界面
 */

public class H5ParamsActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if(intent != null) {
            Uri uri = intent.getData();
            if(uri != null) {
                String host = uri.getHost();
                if(!TextUtils.isEmpty(host)){
                    if(TextUtils.equals("videoinfo",host)){
                        String videoID=uri.getQueryParameter("video_id");
                        if(TextUtils.isEmpty(videoID)){
                            ToastUtils.showCenterToast("错误");
                            finish();
                            return;
                        }
                        VideoDetailsActivity.start(H5ParamsActivity.this,videoID,"",false);
                        finish();
                        return;
                    }else if(TextUtils.equals("userinfo",host)){
                        String authorId=uri.getQueryParameter("author_id");
                        String isFollow=uri.getQueryParameter("is_follow");
                        if(TextUtils.isEmpty(authorId)){
                            ToastUtils.showCenterToast("错误");
                            finish();
                            return;
                        }
                        AuthorDetailsActivity.start(H5ParamsActivity.this,authorId,isFollow);
                        finish();
                        return;
                    }else{
                        ToastUtils.showCenterToast("没有找到可用的通信协议！");
                        onBackPressed();
                    }
                }else{
                    onBackPressed();
                }
            }else{
                onBackPressed();
            }
        }else{
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}

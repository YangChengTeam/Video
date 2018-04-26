package com.video.newqu.util.attach;

import android.content.Intent;
import com.ksyun.media.shortvideo.kit.KSYEditKit;
import com.ksyun.media.shortvideo.utils.ShortVideoConstants;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.util.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2017/11/23.
 * 视频合成
 */

public class VideoComposeTask extends Thread {

    private UploadVideoInfo mComposeTaskInfo;
    private KSYEditKit mKSYEditKit;
    private Timer mTimer;

    public KSYEditKit getEditKti() {
        return mKSYEditKit;
    }

    public VideoComposeTask(UploadVideoInfo composeTaskInfo, KSYEditKit editKit) {
        this.mComposeTaskInfo=composeTaskInfo;
        this.mKSYEditKit=editKit;
    }

    /**
     * 开始合成
     */
    public void execute() {
        if(null!=mComposeTaskInfo){
            Intent intent=new Intent();
            intent.setAction(Constant.ACTION_XINQU_VIDEO_COMPOSE);
            intent.putExtra("action_type",0);
            VideoApplication.getInstance().sendBroadcast(intent,Constant.PERMISSION_VIDEO_COMPOSE);

            if(null!= mKSYEditKit){
                //监听合并的进度
                mKSYEditKit.setOnInfoListener(new KSYEditKit.OnInfoListener() {
                    @Override
                    public Object onInfo(int type, String... strings) {
                        switch (type) {
                            //开始合并文件
                            case ShortVideoConstants.SHORTVIDEO_COMPOSE_START: {
                                composeStarted();
                                return null;
                            }
                            //文件合并文成
                            case ShortVideoConstants.SHORTVIDEO_COMPOSE_FINISHED: {
                                composeSuccess();
                                return null;
                            }
                            default:
                                composeError();
                                return null;
                        }
                    }
                });
                //监听合成的错误情况
                mKSYEditKit.setOnErrorListener(new KSYEditKit.OnErrorListener() {
                    @Override
                    public void onError(int type, long l) {
                        switch (type) {
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FAILED_UNKNOWN:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_CLOSE_FAILED:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_FORMAT_NOT_SUPPORTED:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_OPEN_FAILED:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_WRITE_FAILED:
                                composeError();
                                break;
                            case ShortVideoConstants.SHORTVIDEO_ERROR_SDK_AUTHFAILED:
                                composeError();
                                break;
                            case ShortVideoConstants.SHORTVIDEO_EDIT_PREVIEW_PLAYER_ERROR:
                                composeError();
                            default:
                                composeError();
                                break;
                        }
                    }
                });
                //开始合并
                if(null!=mKSYEditKit&&null!=mComposeTaskInfo){
                    mKSYEditKit.startCompose(mComposeTaskInfo.getCompostOutFilePath());
                }
            }
        }
    }

    /**
     * 合并失败
     */
    private void composeError() {
        if(null!=mTimer){
            mTimer.cancel();
            mTimer=null;
        }
        onDestory();
        //进程通知任务合并失败
        if(null!=mComposeTaskInfo){
            VideoComposeProcessor.getInstance().removeComposeTaskList(mComposeTaskInfo);
            mComposeTaskInfo.setComposeState(Constant.VIDEO_UPLOAD_ERROR);
            Intent intent=new Intent();
            intent.putExtra("video_info",mComposeTaskInfo);
            intent.setAction(Constant.ACTION_XINQU_VIDEO_COMPOSE);
            intent.putExtra("action_type",1);
            VideoApplication.getInstance().sendBroadcast(intent,Constant.PERMISSION_VIDEO_COMPOSE);
        }
    }

    /**
     * 合并开始
     */
    public void composeStarted() {
        if(null!=mComposeTaskInfo){
            mComposeTaskInfo.setComposeState(Constant.VIDEO_COMPOSE_STARTED);
            mComposeTaskInfo.setUploadProgress(0);
            Intent intent=new Intent();
            intent.putExtra("video_info",mComposeTaskInfo);
            intent.setAction(Constant.ACTION_XINQU_VIDEO_COMPOSE);
            intent.putExtra("action_type",1);
            VideoApplication.getInstance().sendBroadcast(intent,Constant.PERMISSION_VIDEO_COMPOSE);
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(null!=mKSYEditKit){
                        final int progress =mKSYEditKit.getProgress();
                        updateProgress(progress);
                    }
                }
            }, 500, 500);
        }
    }

    /**
     * 合成进度
     * @param progress
     */
    private void updateProgress(int progress) {
        if(null!=mComposeTaskInfo){
            mComposeTaskInfo.setComposeState(Constant.VIDEO_COMPOSE_PROGRESS);
            mComposeTaskInfo.setUploadProgress(progress);
            Intent intent=new Intent();
            intent.putExtra("video_info",mComposeTaskInfo);
            intent.setAction(Constant.ACTION_XINQU_VIDEO_COMPOSE);
            intent.putExtra("action_type",1);
            VideoApplication.getInstance().sendBroadcast(intent,Constant.PERMISSION_VIDEO_COMPOSE);
        }
    }

    /**
     * 合并完成
     */
    private void composeSuccess() {
        if(null!=mTimer){
            mTimer.cancel();
            mTimer=null;
        }
        onDestory();
        if(null!=mComposeTaskInfo){
            VideoComposeProcessor.getInstance().removeComposeTaskList(mComposeTaskInfo);
            if(null!=mComposeTaskInfo){
                mComposeTaskInfo.setComposeState(Constant.VIDEO_COMPOSE_FINLISHED);
                mComposeTaskInfo.setUploadProgress(100);
                Intent intent=new Intent();
                intent.putExtra("video_info",mComposeTaskInfo);
                intent.setAction(Constant.ACTION_XINQU_VIDEO_COMPOSE);
                intent.putExtra("action_type",1);
                VideoApplication.getInstance().sendBroadcast(intent,Constant.PERMISSION_VIDEO_COMPOSE);
                addUploadTaskList(mComposeTaskInfo);
            }
        }
    }

    public void onDestory() {
        if(null!=mKSYEditKit){
            mKSYEditKit.stopCompose();
            mKSYEditKit.release();
        }
    }

    /**
     * 合并成功后添加至上传任务列表并立即启动上传程序
     * @param composeTaskInfo
     */
    private void addUploadTaskList(UploadVideoInfo composeTaskInfo) {
        if(null==composeTaskInfo) return;
        composeTaskInfo.setUploadType(100);//默认等待上传中
        try {
            composeTaskInfo.setVideoFileKey(FileUtils.getMd5ByFile(new File(composeTaskInfo.getFilePath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        composeTaskInfo.setItemType(0);
        composeTaskInfo.setVideoName(new File(composeTaskInfo.getFilePath()).getName());
        boolean insertVideoInfo = ApplicationManager.getInstance().getVideoUploadDB().insertNewUploadVideoInfo(composeTaskInfo);
        if (insertVideoInfo) {
            composeTaskInfo.setComposeState(Constant.VIDEO_UPLOAD_STARTED);
            Intent intent=new Intent();
            intent.putExtra("video_info",mComposeTaskInfo);
            intent.setAction(Constant.ACTION_XINQU_VIDEO_COMPOSE);
            intent.putExtra("action_type",1);
            VideoApplication.getInstance().sendBroadcast(intent,Constant.PERMISSION_VIDEO_COMPOSE);
            mComposeTaskInfo=null;
            return;
        }
    }
}

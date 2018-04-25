package com.video.newqu.util.attach;

import com.ksyun.media.shortvideo.kit.KSYEditKit;
import com.ksyun.media.shortvideo.utils.ShortVideoConstants;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2017/11/23.
 * 视频合成
 */

public class VideoComposeTask extends Thread {

    public static final String TAG=VideoComposeTask.class.getSimpleName();
    private UploadVideoInfo mComposeTaskInfo;
    private final WeakReference<KSYEditKit> mEditKtiWeakReference;
    private Timer mTimer;

    public KSYEditKit getEditKti() {
        return mEditKtiWeakReference.get();
    }


    public VideoComposeTask(UploadVideoInfo composeTaskInfo, KSYEditKit editKit) {
        this.mComposeTaskInfo=composeTaskInfo;
        mEditKtiWeakReference = new WeakReference<KSYEditKit>(editKit);
    }
    /**
     * 开始合成
     */
    public void execute() {
        if(null!=mComposeTaskInfo){
            if(null!= mEditKtiWeakReference.get()){
                //监听合并的进度
                mEditKtiWeakReference.get().setOnInfoListener(new KSYEditKit.OnInfoListener() {
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
                                composeFilished();
                                return null;
                            }
                            default:
                                composeFilished();
                                return null;
                        }
                    }
                });
                //监听合成的错误情况
                mEditKtiWeakReference.get().setOnErrorListener(new KSYEditKit.OnErrorListener() {
                    @Override
                    public void onError(int type, long l) {
                        switch (type) {
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FAILED_UNKNOWN:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_CLOSE_FAILED:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_FORMAT_NOT_SUPPORTED:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_OPEN_FAILED:
                            case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_WRITE_FAILED:
                                composeFilished();
                                break;
                            case ShortVideoConstants.SHORTVIDEO_ERROR_SDK_AUTHFAILED:
                                composeFilished();
                                break;
                            case ShortVideoConstants.SHORTVIDEO_EDIT_PREVIEW_PLAYER_ERROR:
                                composeFilished();
                            default:
                                composeFilished();
                                break;
                        }
                    }
                });
                //开始合并
                if(null!=mComposeTaskInfo){
                    mEditKtiWeakReference.get().startCompose(mComposeTaskInfo.getCompostOutFilePath());
                }
            }
        }
    }

    /**
     * 合并开始
     */
    public void composeStarted() {
        if(null!=mComposeTaskInfo){
            mComposeTaskInfo.setComposeState(Constant.VIDEO_COMPOSE_STARTED);
            mComposeTaskInfo.setUploadProgress(0);
            EventBus.getDefault().post(mComposeTaskInfo);
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(null!=mEditKtiWeakReference.get()){
                        final int progress =mEditKtiWeakReference.get().getProgress();
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
            EventBus.getDefault().post(mComposeTaskInfo);
        }
    }

    /**
     * 合并完成
     */
    private void composeFilished() {

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
                EventBus.getDefault().post(mComposeTaskInfo);
                addUploadTaskList(mComposeTaskInfo);
            }
        }
    }

    public void onDestory() {
        if(null!=mEditKtiWeakReference.get()){
            mEditKtiWeakReference.get().stopCompose();
            mEditKtiWeakReference.get().release();
            mEditKtiWeakReference.clear();
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
            EventBus.getDefault().post(composeTaskInfo);
            return;
        }
    }
}

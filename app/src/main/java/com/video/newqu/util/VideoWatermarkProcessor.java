package com.video.newqu.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ksyun.media.shortvideo.kit.KSYEditKit;
import com.ksyun.media.shortvideo.utils.ShortVideoConstants;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2017/11/20.
 * 视频合并处理工具类，合成完毕开始上传,支持批量合成、单个取消
 */

public class VideoWatermarkProcessor {

    private static final int COMPOSE_START = 0;
    private static final int COMPOSE_PROGRESS = 2;
    private static final int COMPOSE_FINLISHS = 3;
    private static final int COMPOSE_ERROR = 4;
    public static VideoWatermarkProcessor cVideoComposeProcessor;
    private String finalOutputPath;
    private String mLogoPath = "assets://XinQuLogo/logo.png";

    public interface OnComposeTaskListener{
        void onComposeStart();
        void onComposeProgress(int progress);
        void onComposeFinlish(String outFilePath);
        void onComposeError(String errorMsg);
    }

    public void setOnComposeTaskListener(OnComposeTaskListener onComposeTaskListener) {
        mOnComposeTaskListener = onComposeTaskListener;
    }

    private OnComposeTaskListener mOnComposeTaskListener;


    public static synchronized VideoWatermarkProcessor getInstance(){
        synchronized(VideoWatermarkProcessor.class){
            if(null==cVideoComposeProcessor){
                cVideoComposeProcessor=new VideoWatermarkProcessor();
            }
        }
        return cVideoComposeProcessor;
    }

    /**
     * 添加视频合成任务
     */
    public void addVideoComposeTask(Context context,String resourceFilePath, String outPutPath,OnComposeTaskListener onComposeTaskListener){
        if(TextUtils.isEmpty(resourceFilePath)) return;
        if(TextUtils.isEmpty(outPutPath))return;
        this.mOnComposeTaskListener=onComposeTaskListener;
        VideoWaterMarkComposeTask videoComposeTask = new VideoWaterMarkComposeTask(context,resourceFilePath,outPutPath);
        videoComposeTask.execute();
    }


    /**
     * 通信桥梁
     */
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMPOSE_START:
                    if(null!=mOnComposeTaskListener){
                        mOnComposeTaskListener.onComposeStart();
                    }
                    break;
                case COMPOSE_PROGRESS:
                    if(null!=mOnComposeTaskListener){
                        mOnComposeTaskListener.onComposeProgress(msg.arg1);
                    }
                    break;
                case COMPOSE_FINLISHS:
                    if(null!=mOnComposeTaskListener){
                        mOnComposeTaskListener.onComposeFinlish(finalOutputPath);
                    }
                    break;
                case COMPOSE_ERROR:
                    if(null!=mOnComposeTaskListener){
                        mOnComposeTaskListener.onComposeError("合并失败");
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };



    private class VideoWaterMarkComposeTask extends Thread {

        private final WeakReference<KSYEditKit> mEditKtiWeakReference;
        private final String resourceFilePath;
        private final String outPutPath;
        private Timer mTimer;


        public VideoWaterMarkComposeTask(Context context, String resourceFilePath, String outPutPath) {
            KSYEditKit ksyEditKit=new KSYEditKit(context);
            mEditKtiWeakReference = new WeakReference<KSYEditKit>(ksyEditKit);
            this.resourceFilePath=resourceFilePath;
            this.outPutPath=outPutPath;
        }
        /**
         * 开始合成
         */
        public void execute() {

            if(null!=mEditKtiWeakReference&&null!=mEditKtiWeakReference.get()){
                mEditKtiWeakReference.get().setEditPreviewUrl(resourceFilePath);
                StringBuilder composeUrl = new StringBuilder(outPutPath).append("/").append(Utils.getFileName(resourceFilePath));//这里的最终文件保存路径需要和视频本身名称名称一致

                VideoWatermarkProcessor.this.finalOutputPath=composeUrl.toString();
                mEditKtiWeakReference.get().pauseEditPreview();
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

                //设置水印
                mEditKtiWeakReference.get().showWaterMarkLogo(mLogoPath, 0.77f, 0.02f, 0.20f, 0, 0.8f);
                //开始合并
                mEditKtiWeakReference.get().startCompose(finalOutputPath);
            }
        }

        /**
         * 合并开始
         */
        public void composeStarted() {

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
            if(null!=mHandler){
                Message message=Message.obtain();
                message.what=COMPOSE_START;
                mHandler.sendMessage(message);
            }
        }

        /**
         * 合成进度
         * @param progress
         */
        private void updateProgress(int progress) {
            if(null!=mHandler){
                Message message=Message.obtain();
                message.what=COMPOSE_PROGRESS;
                message.arg1=progress;
                mHandler.sendMessage(message);
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
            if(null!=mHandler){
                Message message=Message.obtain();
                message.what=COMPOSE_FINLISHS;
                mHandler.sendMessage(message);
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
            if(null!=mHandler){
                Message message=Message.obtain();
                message.what=COMPOSE_ERROR;
                mHandler.sendMessage(message);
            }
        }

        public void onDestory() {
            if(null!=mEditKtiWeakReference.get()){
                mEditKtiWeakReference.get().stopCompose();
                mEditKtiWeakReference.get().release();
                mEditKtiWeakReference.clear();
            }
        }
    }
}

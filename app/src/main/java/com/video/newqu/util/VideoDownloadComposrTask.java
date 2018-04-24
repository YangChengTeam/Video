package com.video.newqu.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.video.newqu.VideoApplication;
import com.video.newqu.contants.Constant;
import com.video.newqu.ui.dialog.DownloadProgressDialog;
import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2017/12/9
 * 这个类负责下载整套流程，检查缓存，复制，下载，添加水印
 */

public class VideoDownloadComposrTask {

    private DownloadProgressDialog mUploadProgressView;
    private final Activity mContext;
    private final String mFileNetPath;
    private boolean isKSYPermission=true;//是否获得金山云授权

    public VideoDownloadComposrTask(Activity context, String fileNetPath) {
        if(context instanceof Activity){
            this.mContext=context;
            this.mFileNetPath=fileNetPath;
        }else {
            throw new IllegalStateException("Error! You must preset so Activity Context!");
        }
    }

    public void start() {
        File watermarkFileDirPath=new File(Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);//XinQu/Video/目录下
        if(!watermarkFileDirPath.exists()){
            watermarkFileDirPath.mkdirs();
        }

        File file=new File(Constant.DOWNLOAD_PATH);//XinQu/File/Download/目录下
        if(!file.exists()){
            file.mkdirs();
        }
        createDownload();
    }
    /**
     * 下载之前的准备工作
     */
    private void createDownload() {
        //文件地址可能不是.mp4结尾的，需要判断下 rexVideoPath(name);
        File watermarkFilePath=new File(Constant.DOWNLOAD_WATERMARK_VIDEO_PATH,Utils.rexVideoPath(Utils.getFileName(mFileNetPath)));
        //先判断添加水印之后的视频中是否存在将要下载的视频文件
        if(watermarkFilePath.exists()&&watermarkFilePath.isFile()){
            if(null!=mContext) ToastUtils.showFinlishToast((AppCompatActivity)mContext,null,null,"已保存至本地:"+Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);
        }else{
            //文件地址可能不是.mp4结尾的，需要判断下 rexVideoPath(name);
            File filePath=new File(Constant.DOWNLOAD_PATH,Utils.rexVideoPath(Utils.getFileName(mFileNetPath)));
            //下载文件夹中存在此视频文件,直接合并就行
            if(filePath.exists()&&filePath.getName().endsWith(".mp4")){
                //添加到合并任务中,并直接开始
                startCompos(filePath.getAbsolutePath(), Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);
            }else{
                //判断缓存文件是否存在完整的视频文件
                HttpProxyCacheServer proxy = VideoApplication.getProxy();
                File cacheFile = proxy.getCacheFile(mFileNetPath);
                //本地缓存存在完成视频，直接复制到下载的目录，后直接合并
                if(null!=cacheFile&&cacheFile.exists()&&cacheFile.isFile()&&Utils.isFileToMp4(cacheFile.getAbsolutePath())){
                    startCopyFile(cacheFile.getAbsolutePath(),Constant.DOWNLOAD_PATH);
                }else{
                    //合成目录、下载目录、缓存文件都不存在，开始下载
                    startDownloadTask();
                }
            }
        }
    }

    /**
     * 开始复制文件到目标文件夹下
     * @param resouceFilePath 源文件的绝对地址
     * @param outFilePath 要复制到所在目录的相对路径
     */

    private void startCopyFile(String resouceFilePath, String outFilePath) {
        if(TextUtils.isEmpty(resouceFilePath))return;
        if(TextUtils.isEmpty(outFilePath))return;
        File outPath=new File(outFilePath);
        if(!outPath.exists()){
            outPath.mkdirs();
        }

        File fileOutPath=new File(outFilePath,Utils.getFileName(mFileNetPath));
        new CopyFileTask(fileOutPath.getAbsolutePath()).execute(resouceFilePath);
    }

    /**
     * 复制文件
     */
    private class CopyFileTask extends AsyncTask<String,Void,Boolean> {

        private final String mOutFilePath;

        public CopyFileTask(String outFilePath) {
            this.mOutFilePath=outFilePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDownloadTips("保存至本地中..");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if(null!=params&&params.length>0){
                return FileUtils.copyFile(params[0], mOutFilePath);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(null!=aBoolean&&aBoolean){
                if(isKSYPermission){
                    File file=new File(mOutFilePath);
                    if(file.exists()&&file.isFile()){

                        startCompos(mOutFilePath, Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);
                    }else{

                        startDownloadTask();
                    }
                }else{
                    isKSYPermission=true;
                    ToastUtils.showCenterToast("下载完成，可以分享啦！");
                }

            }else{

                startDownloadTask();
            }
        }
    }

    /**
     * 开始下载
     */
    private void startDownloadTask() {

        File file=new File(Constant.DOWNLOAD_PATH,Utils.getFileName(mFileNetPath));
        if(null!=file&&file.exists()){
            FileUtils.deleteFile(file);
        }
        //下载视频类型文件
        new FileDownloadTask(FileDownloadTask.FILE_TYPE_VIDEO,Constant.DOWNLOAD_PATH, new FileDownloadTask.OnDownloadListener() {
            @Override
            public void downloadStart() {

                showDownloadTips("视频下载中...");
            }

            @Override
            public void downloadProgress(int progress) {
                if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                    mUploadProgressView.setProgress(progress);
                }
            }

            @Override
            public void downloadFinlish(File file) {
                if(null==mUploadProgressView){
                    showDownloadTips("保存至本地中...");
                }
                startCompos(file.getAbsolutePath(), Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);
            }

            @Override
            public void downloadError(String errorMessage) {
                clodeDownloadProgress();
                ToastUtils.showCenterToast(errorMessage);
            }
        }).execute(mFileNetPath);
    }

    /**
     * 开始合并
     */
    private void startCompos(String resourceFilePath, String outPutPath) {
        //判断是否取得授权，若未取得授权，直接复制文件到目录中去
        if(AuthInfoManager.getInstance().getAuthState()){
            VideoWatermarkProcessor.getInstance().addVideoComposeTask(mContext,resourceFilePath, outPutPath, new VideoWatermarkProcessor.OnComposeTaskListener() {
                @Override
                public void onComposeStart() {

                    showDownloadTips("保存至本地中..");
                }

                @Override
                public void onComposeProgress(int progress) {
                    if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                        mUploadProgressView.setProgress(progress);
                    }
                }

                @Override
                public void onComposeFinlish(String outPath) {
                    if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                        mUploadProgressView.setTipsMessage("保存完成");
                    }
                    clodeDownloadProgress();
                    Uri localUri = Uri.parse("file://"+ outPath);
                    Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    localIntent.setData(localUri);
                    mContext.sendBroadcast(localIntent);
                    if(null!=mContext) ToastUtils.showFinlishToast((AppCompatActivity)mContext,null,null,"已保存至本地:"+Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);
                }

                @Override
                public void onComposeError(String errorMsg) {
                    if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                        mUploadProgressView.setTipsMessage("errorMsg");
                    }
                    clodeDownloadProgress();
                    ToastUtils.showCenterToast(errorMsg);
                }
            });
        }else{
            isKSYPermission=false;
            startCopyFile(resourceFilePath,outPutPath);
        }
    }


    /**
     * 显示下载\合并 进度条
     */
    private void showDownloadTips(String message){
        if(null!=mContext){
            if(null==mUploadProgressView){
                mUploadProgressView = new DownloadProgressDialog(mContext);
                mUploadProgressView.setOnDialogBackListener(new DownloadProgressDialog.OnDialogBackListener() {
                    @Override
                    public void onBack() {
                        ToastUtils.showCenterToast("请等待保存至相册完成!");
                    }
                });
                mUploadProgressView.setMax(100);
            }
            mUploadProgressView.setProgress(0);
            mUploadProgressView.setTipsMessage(message);
            if(!mUploadProgressView.isShowing()){
                mUploadProgressView.show();
            }
        }
    }

    /**
     * 关闭下载\合并 进度条
     */
    private void clodeDownloadProgress(){
        if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
            mUploadProgressView.dismiss();
            mUploadProgressView=null;
        }
    }
}
